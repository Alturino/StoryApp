package com.onirutla.storyapp.data.source.repository.story

import androidx.paging.PagingData
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.PageResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface StoryRepository {

    suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): BaseResponse

    suspend fun getStoriesWithTokenAndLocation(token: String): PageResponse<StoryResponse>

    fun getAllStoriesWithToken(token: String): Flow<PagingData<StoryResponse>>
}