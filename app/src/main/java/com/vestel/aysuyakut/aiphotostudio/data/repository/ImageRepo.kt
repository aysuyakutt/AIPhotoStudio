package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.net.Uri
import com.vestel.aysuyakut.aiphotostudio.data.db.entity.ImageEntity
import kotlinx.coroutines.flow.Flow


interface ImageRepo {
    fun getAll(): Flow<List<ImageEntity>>
    suspend fun saveUri(picked: Uri?): Long
    suspend fun delete(id: Long,uri: Uri): Boolean
}

