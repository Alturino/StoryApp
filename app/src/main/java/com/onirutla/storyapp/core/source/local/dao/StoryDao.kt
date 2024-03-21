package com.onirutla.storyapp.core.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.onirutla.storyapp.core.source.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Upsert
    fun upsertStory(vararg story: StoryEntity)

    @Delete
    fun deleteStory(vararg story: StoryEntity)

    @RawQuery(observedEntities = [StoryEntity::class])
    @Transaction
    fun getStoriesPaging(query: SupportSQLiteQuery): PagingSource<Int, StoryEntity>

    @Query(value = "select * from stories where (lon == null or lon == 0.0) and (lat == null or lat == 0.0)")
    @Transaction
    fun getStoriesWithoutLocationPaging(): PagingSource<Int, StoryEntity>

    @Query(value = "select * from stories where (lon != null or lon != 0.0) and (lat != null or lon != 0.0)")
    @Transaction
    fun getStoriesWithLocationPaging(): PagingSource<Int, StoryEntity>

    @Query(value = "delete from stories")
    suspend fun clearStories()
}