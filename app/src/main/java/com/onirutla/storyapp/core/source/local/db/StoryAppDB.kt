package com.onirutla.storyapp.core.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onirutla.storyapp.core.source.local.dao.RemoteKeyDao
import com.onirutla.storyapp.core.source.local.entity.RemoteKeyEntity
import com.onirutla.storyapp.core.source.local.type_converter.InstantTypeConverter
import com.onirutla.storyapp.core.source.local.dao.StoryDao
import com.onirutla.storyapp.core.source.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeyEntity::class], version = 1, exportSchema = true)
@TypeConverters(value = [InstantTypeConverter::class])
abstract class StoryAppDB : RoomDatabase() {
    abstract val storyDao: StoryDao
    abstract val remoteKeyDao: RemoteKeyDao
}