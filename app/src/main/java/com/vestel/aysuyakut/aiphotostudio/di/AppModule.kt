package com.vestel.aysuyakut.aiphotostudio.di

import android.content.Context
import androidx.room.Room
import com.vestel.aysuyakut.aiphotostudio.data.db.dao.ImageDao
import com.vestel.aysuyakut.aiphotostudio.data.db.database.AIPhotoStudioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AIPhotoStudioDatabase =
        Room.databaseBuilder(ctx, AIPhotoStudioDatabase::class.java, "app.db").build()

    @Provides
    fun provideImageDao(db: AIPhotoStudioDatabase): ImageDao = db.imageDao()

}
