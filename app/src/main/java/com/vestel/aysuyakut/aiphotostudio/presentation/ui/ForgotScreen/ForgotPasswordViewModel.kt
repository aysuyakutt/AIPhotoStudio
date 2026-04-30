package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ForgotScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    data class UiState(
        val email: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val sent: Boolean = false,
        val message: String? = null
    )

    var ui by mutableStateOf(UiState())
        private set

    fun onEmailChange(v: String) {
        ui = ui.copy(email = v, emailError = null, message = null)
    }

    fun sendReset() {
        val email = ui.email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ui = ui.copy(emailError = "Please enter a valid email.")
            return
        }

        ui = ui.copy(isLoading = true, message = null)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ui = ui.copy(
                        isLoading = false,
                        sent = true,
                        message = "Reset link sent. Please check your inbox."
                    )
                } else {
                    val msg = mapError(task.exception)
                    ui = ui.copy(isLoading = false, message = msg)
                }
            }
    }

    private fun mapError(e: Exception?): String {
        val fe = e as? com.google.firebase.auth.FirebaseAuthException
        return when (fe?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address."
            "ERROR_USER_NOT_FOUND" -> "No user found with this email."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your connection."
            else -> fe?.localizedMessage ?: (e?.localizedMessage ?: "Something went wrong.")
        }
    }
}
