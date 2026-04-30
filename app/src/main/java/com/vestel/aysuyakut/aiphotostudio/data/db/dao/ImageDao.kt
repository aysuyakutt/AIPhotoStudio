package com.vestel.aysuyakut.aiphotostudio.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vestel.aysuyakut.aiphotostudio.data.db.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("""
        SELECT * FROM images
        WHERE (ownerId = :ownerId) OR (:ownerId IS NULL AND ownerId IS NULL)
        ORDER BY createdAt DESC
    """)
    fun getAll(ownerId: String?): Flow<List<ImageEntity>>

    @Insert
    suspend fun insert(entity: ImageEntity): Long

    @Query("""
        DELETE FROM images
        WHERE id = :id AND ((ownerId = :ownerId) OR (:ownerId IS NULL AND ownerId IS NULL))
    """)
    suspend fun deleteById(id: Long, ownerId: String?): Int
}