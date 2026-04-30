package com.vestel.aysuyakut.aiphotostudio.data.mapper

import android.net.Uri
import com.vestel.aysuyakut.aiphotostudio.data.db.entity.ImageEntity
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen.OldPhoto

fun ImageEntity.toUi(): OldPhoto {
    return OldPhoto(
        id = id,
        uri = Uri.parse(uri),
        ownerId = ownerId ?: "guest",
        createdAt = createdAt
    )
}