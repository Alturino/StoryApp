package com.onirutla.storyapp.auth.di

import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.auth.data.source.remote.api_services.AuthApiService
import com.onirutla.storyapp.auth.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.ktor.client.HttpClient

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {
    @Provides
    @ViewModelScoped
    fun provideAuthApiService(ktorClient: HttpClient): AuthApiService =
        AuthApiService(ktorClient = ktorClient)

    @Provides
    @ViewModelScoped
    fun provideAuthRepositoryImpl(
        apiService: AuthApiService,
        userSessionManager: UserSessionManager,
    ): AuthRepositoryImpl =
        AuthRepositoryImpl(apiService, userSessionManager)
}