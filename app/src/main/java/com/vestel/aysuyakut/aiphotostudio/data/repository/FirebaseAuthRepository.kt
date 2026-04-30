package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.vestel.aysuyakut.aiphotostudio.R
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val appContext: Context
) : AuthRepository {

    override suspend fun isLoggedIn(): Boolean {
        val u = auth.currentUser
        return u != null && !u.isAnonymous
    }
    override suspend fun signUp(fullName: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("User is null after signUp")

        val cleanFull = fullName.trim().replace("\\s+".toRegex(), " ")
        user.updateProfile(userProfileChangeRequest { displayName = cleanFull }).await()

        val first = cleanFull.substringBefore(' ').trim()
        val last  = cleanFull.substringAfter(' ', missingDelimiterValue = "").trim()

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                withTimeoutOrNull(4_000) {
                    FirebaseFirestore.getInstance()
                        .collection("users").document(user.uid)
                        .set(
                            mapOf(
                                "uid" to user.uid,
                                "email" to (user.email ?: email),
                                "displayName" to cleanFull,
                                "firstName" to first,
                                "lastName"  to last,
                                "provider"  to "password",
                                "updatedAt" to FieldValue.serverTimestamp()
                            ),
                            SetOptions.merge()
                        ).await()
                }
            }.onFailure {
                android.util.Log.w("AuthRepo", "Firestore profile save failed: ${it.message}")
            }
        }
    }

    override suspend fun login(email: String, password: String) {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        val user = res.user ?: return
        runCatching { ensureDisplayNameFromProfile(user) }
    }

    private suspend fun getUserDocSafe(uid: String): DocumentSnapshot? {
        val ref = FirebaseFirestore.getInstance().collection("users").document(uid)
        return try {
            ref.get().await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE ||
                e.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                try { ref.get(Source.CACHE).await() } catch (_: Exception) { null }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun ensureDisplayNameFromProfile(user: com.google.firebase.auth.FirebaseUser) {
        val current = normalizeName(user.displayName)

        val snap = getUserDocSafe(user.uid)

        val fsDisplay = normalizeName(snap?.getString("displayName"))
        val fsFirst   = snap?.getString("firstName")
        val fsLast    = snap?.getString("lastName")
        val fsJoined  = normalizeName(fsDisplay ?: joinName(fsFirst, fsLast))

        val target = current ?: fsJoined

        if (!target.isNullOrBlank() && target != current) {
            runCatching {
                val req = userProfileChangeRequest { displayName = target }
                user.updateProfile(req).await()
            }
        }

        if (!target.isNullOrBlank() && fsDisplay.isNullOrBlank()) {
            runCatching {
                FirebaseFirestore.getInstance()
                    .collection("users").document(user.uid)
                    .set(mapOf("displayName" to target), SetOptions.merge())
                    .await()
            }
        }
    }

    private fun joinName(given: String?, family: String?): String? {
        val parts = listOfNotNull(given?.trim(), family?.trim()).filter { it.isNotEmpty() }
        return if (parts.isEmpty()) null else parts.joinToString(" ")
    }
    private fun normalizeName(s: String?): String? =
        s?.trim()?.replace("\\s+".toRegex(), " ")

    override suspend fun loginAsGuest() {
        auth.signInAnonymously().await()
    }

    override suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun loginWithGoogle(activity: Activity) {
        val cm = CredentialManager.create(activity)
        val serverClientId = activity.getString(R.string.default_web_client_id)

        val googleOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val token = try {
            withTimeout(45_000) { // ⏱️ 45 sn
                val result = cm.getCredential(activity, request)
                val cred = result.credential
                if (cred is CustomCredential &&
                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    android.util.Log.d("AuthRepo", "[A] Google credential OK")
                    GoogleIdTokenCredential.createFrom(cred.data)
                } else error("No Google ID token credential.")
            }
        } catch (e: Exception) {
            when (e) {
                is kotlinx.coroutines.TimeoutCancellationException ->
                    throw IllegalStateException("Google account picker took too long (stage A).")
                is androidx.credentials.exceptions.GetCredentialException ->
                    throw IllegalStateException("Google sign-in failed: ${e.type}", e)
                else -> throw e
            }
        }

        val user = try {
            withTimeout(45_000) { // ⏱️ 45 sn
                val firebaseCred = GoogleAuthProvider.getCredential(token.idToken, null)
                val res = auth.signInWithCredential(firebaseCred).await()
                android.util.Log.d("AuthRepo", "[B] Firebase signInWithCredential OK")
                res.user ?: error("No Firebase user after sign-in")
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.TimeoutCancellationException) {
                throw IllegalStateException("Firebase sign-in took too long (stage B).")
            }
            throw e
        }

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            runCatching {
                if (user.displayName.isNullOrBlank() && !token.displayName.isNullOrBlank()) {
                    val req = userProfileChangeRequest { displayName = token.displayName }
                    user.updateProfile(req).await()
                }
                FirebaseFirestore.getInstance().collection("users").document(user.uid)
                    .set(
                        mapOf(
                            "uid" to user.uid,
                            "email" to (user.email ?: ""),
                            "displayName" to (token.displayName ?: ""),
                            "provider" to "google",
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    ).await()
            }.onFailure { android.util.Log.w("AuthRepo", "Post-sync failed: ${it.message}") }
        }
    }

    override suspend fun logout(revokeGoogle: Boolean) {
        runCatching { Identity.getSignInClient(appContext).signOut().await() }
        auth.signOut()
    }
}
