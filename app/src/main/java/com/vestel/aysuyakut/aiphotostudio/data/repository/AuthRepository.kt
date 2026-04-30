package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.app.Activity

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun login(email: String, password: String)
    suspend fun signUp(fullName: String, email: String, password: String)
    suspend fun loginAsGuest()
    suspend fun sendPasswordReset(email: String)
    suspend fun loginWithGoogle(activity: Activity)
    suspend fun logout(revokeGoogle: Boolean = false)

}