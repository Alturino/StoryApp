package com.onirutla.storyapp.story.di

import com.onirutla.storyapp.story.data.repository.StoryRepositoryImpl
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindStoryModule {

    @Binds
    @Singleton
    abstract fun bindStoryRepositoryImpl(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository

}