package com.vestel.aysuyakut.aiphotostudio.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vestel.aysuyakut.aiphotostudio.data.db.dao.ImageDao
import com.vestel.aysuyakut.aiphotostudio.data.db.entity.ImageEntity

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false )
abstract class AIPhotoStudioDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

}