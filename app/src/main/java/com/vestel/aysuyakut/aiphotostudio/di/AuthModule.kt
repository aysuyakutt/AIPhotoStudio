package com.vestel.aysuyakut.aiphotostudio.di

import com.vestel.aysuyakut.aiphotostudio.data.repository.AuthRepository
import com.vestel.aysuyakut.aiphotostudio.data.repository.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository
}
