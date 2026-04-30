package com.vestel.aysuyakut.aiphotostudio.di

import android.content.Context
import com.vestel.aysuyakut.aiphotostudio.data.repository.AiRepo
import com.vestel.aysuyakut.aiphotostudio.data.repository.AiRepoFirebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  AppBindingsModule {
    @Provides
    @Singleton
    fun provideAiRepo(@ApplicationContext ctx: Context): AiRepo = AiRepoFirebase(ctx)
}
