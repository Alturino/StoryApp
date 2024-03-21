package com.onirutla.storyapp.di

import android.content.Context
import androidx.room.Room
import com.onirutla.storyapp.core.source.local.dao.StoryDao
import com.onirutla.storyapp.core.source.local.db.StoryAppDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestLocalModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryAppDB {
        return Room.inMemoryDatabaseBuilder(
            context,
            StoryAppDB::class.java,
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(db: StoryAppDB): StoryDao = db.storyDao
}