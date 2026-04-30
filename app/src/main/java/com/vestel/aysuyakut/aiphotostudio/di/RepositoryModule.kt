package com.vestel.aysuyakut.aiphotostudio.di

import com.vestel.aysuyakut.aiphotostudio.data.repository.ImageRepositoryImpl
import com.vestel.aysuyakut.aiphotostudio.data.repository.ImageRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        impl: ImageRepositoryImpl
    ): ImageRepo
}