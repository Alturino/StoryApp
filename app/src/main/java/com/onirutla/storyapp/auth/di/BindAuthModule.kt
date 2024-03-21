package com.onirutla.storyapp.auth.di

import com.onirutla.storyapp.auth.data.repository.AuthRepositoryImpl
import com.onirutla.storyapp.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class BindAuthModule {

    @Binds
    @ViewModelScoped
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

}