package com.vestel.aysuyakut.aiphotostudio.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val ownerId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
