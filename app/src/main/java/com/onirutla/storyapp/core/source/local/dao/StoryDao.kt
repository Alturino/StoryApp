/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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