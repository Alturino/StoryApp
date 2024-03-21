package com.onirutla.storyapp.story.di

import com.onirutla.storyapp.core.source.local.db.StoryAppDB
import com.onirutla.storyapp.story.data.repository.StoryRepositoryImpl
import com.onirutla.storyapp.story.data.source.remote.api_services.StoryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoryModule {

    @Provides
    @Singleton
    fun provideStoryApiService(ktorClient: HttpClient): StoryApiService =
        StoryApiService(ktorClient = ktorClient)

    @Provides
    @Singleton
    fun provideStoryRepositoryImpl(
        apiService: StoryApiService,
        storyAppDB: StoryAppDB,
    ): StoryRepositoryImpl = StoryRepositoryImpl(
        apiService = apiService,
        db = storyAppDB
    )

}