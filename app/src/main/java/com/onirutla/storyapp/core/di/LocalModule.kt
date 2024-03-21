package com.onirutla.storyapp.core.di

import android.content.Context
import androidx.room.Room
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.source.local.db.StoryAppDB
import com.onirutla.storyapp.core.source.local.dao.StoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryAppDB {
        return Room.databaseBuilder(
            context,
            StoryAppDB::class.java,
            context.getString(R.string.app_name)
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(db: StoryAppDB): StoryDao = db.storyDao

}