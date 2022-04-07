package com.onirutla.storyapp.di

import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.story.StoryRepositoryImpl
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

}