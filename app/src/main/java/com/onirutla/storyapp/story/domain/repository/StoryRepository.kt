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

package com.onirutla.storyapp.story.domain.repository

import androidx.paging.PagingData
import arrow.core.Either
import com.onirutla.storyapp.core.domain.ResponseState
import com.onirutla.storyapp.core.domain.secrets.Secret
import com.onirutla.storyapp.core.source.remote.model.BaseResponse
import com.onirutla.storyapp.story.data.source.remote.model.request.StoryRequest
import com.onirutla.storyapp.story.domain.data.Story
import com.onirutla.storyapp.story.domain.data.StoryFilterType
import com.onirutla.storyapp.story.domain.data.StorySortType
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    fun getStoryById(id: String, token: String): Flow<ResponseState<Story>>

    fun getStoriesFlow(token: String): Flow<ResponseState<List<Story>>>
    suspend fun getStories(
        page: Int = Secret.STORY_API_STARTING_INDEX,
        size: Int = 1,
        token: String,
        withLocation: Boolean = false,
    ): ResponseState<List<Story>>

    suspend fun uploadStory(
        request: StoryRequest,
        token: String,
        progress: suspend (sent: Long, length: Long) -> Unit,
    ): Either<Throwable, BaseResponse<Unit>>

    fun getStoriesPaging(
        isOnline: Boolean,
        filter: StoryFilterType = StoryFilterType.All,
        sort: StorySortType = StorySortType.NameAscending,
        token: String,
        size: Int = Secret.NETWORK_LOAD_SIZE,
    ): Flow<PagingData<Story>>
}