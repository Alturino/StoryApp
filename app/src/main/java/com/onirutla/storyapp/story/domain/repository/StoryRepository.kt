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