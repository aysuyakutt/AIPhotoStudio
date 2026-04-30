package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen

import androidx.annotation.DrawableRes

data class ProfileUiState(
    val userName: String = "",
    val userInfo: String = "",
    @DrawableRes val profileImageRes: Int? = null,
    val photos: List<OldPhoto> = emptyList(),
    val selectedPhoto: OldPhoto? = null,
    val isDrawerOpen: Boolean = false
)
