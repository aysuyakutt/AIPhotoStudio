package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen

import android.net.Uri

data class OldPhoto(
    val id: Long,
    val uri: Uri,
    val ownerId: String?,
    val createdAt: Long
)
