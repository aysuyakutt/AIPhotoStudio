// presentation/ui/LoginScreen/AuthViewModel.kt
package com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vestel.aysuyakut.aiphotostudio.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _events = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AuthEvent> = _events

    suspend fun isLoggedIn(): Boolean = repo.isLoggedIn()

    fun login(email: String, password: String) = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.login(email, password) }
            .onSuccess { _events.tryEmit(AuthEvent.Success) } // tryEmit ✅
            .onFailure { e -> _events.tryEmit(AuthEvent.Error(parseAuthError(e))) }
        _loading.value = false
    }

    fun signUp(fullName: String, email: String, password: String) = viewModelScope.launch {
        _loading.value = true
        try {
            repo.signUp(fullName, email, password)
            _events.tryEmit(AuthEvent.Success)
        } catch (e: Exception) {
            _events.tryEmit(AuthEvent.Error(parseAuthError(e)))
        } finally {
            _loading.value = false
        }
    }



    fun loginWithGoogle(activity: android.app.Activity) = viewModelScope.launch {
        _loading.value = true
        try {
            repo.loginWithGoogle(activity)
            _events.tryEmit(AuthEvent.Success)
        } catch (t: Throwable) {
            val msg = when (t) {
                is androidx.credentials.exceptions.GetCredentialException ->
                    "Google sign-in failed: ${t.type}"
                else -> t.message ?: "Google sign-in failed"
            }
            _events.tryEmit(AuthEvent.Error(msg))
        } finally {
            _loading.value = false
        }
    }


    fun continueAsGuest() = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.loginAsGuest() }
            .onSuccess { _events.tryEmit(AuthEvent.Success) }
            .onFailure { e -> _events.tryEmit(AuthEvent.Error(e.message ?: "Guest login failed")) }
        _loading.value = false
    }

    fun sendReset(email: String) = viewModelScope.launch {
        _loading.value = true
        runCatching { repo.sendPasswordReset(email) }
            .onSuccess { _events.tryEmit(AuthEvent.Success) }
            .onFailure { e -> _events.tryEmit(AuthEvent.Error(e.message ?: "Reset email failed")) }
        _loading.value = false
    }

    private fun parseAuthError(t: Throwable): String {
        // Bubble up Firebase auth error codes when available
        return when (t) {
            is com.google.firebase.FirebaseNetworkException ->
                "Network error. Please check your internet connection."
            is com.google.firebase.FirebaseTooManyRequestsException ->
                "Too many attempts. Please try again later."
            is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                "Weak password. " + (t.reason ?: "Please choose a stronger password.")
            is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                "This email is already in use."
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> when (t.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email address."
                "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Email or password is incorrect."
                else -> "Invalid credentials."
            }
            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> when (t.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                "ERROR_USER_DISABLED" -> "This account has been disabled."
                else -> "User account error."
            }
            is com.google.firebase.auth.FirebaseAuthException -> when (t.errorCode) {
                "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is disabled in Firebase console."
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already in use."
                else -> "Auth error: ${t.errorCode}"
            }
            else -> t.message ?: "Sign-in failed. Please try again."
        }
    }
}

sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Error(val msg: String) : AuthEvent
}
