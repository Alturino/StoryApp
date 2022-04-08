package com.onirutla.storyapp.data.source.repository.story

import androidx.paging.PagingData
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {
    suspend fun addNewStoryWithToken(image: File, token: String): BaseResponse

    fun getAllStoriesWithToken(token: String): Flow<PagingData<StoryResponse>>
}