package com.onirutla.storyapp.data.source.repository.story

import androidx.paging.PagingData
import com.onirutla.storyapp.data.DataDummy
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MultipartBody

object FakeStoryRepository : StoryRepository {

    override suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): BaseResponse {
        if (token.isEmpty()) {
            throw IllegalArgumentException()
        }
        return BaseResponse(error = false)
    }

    override fun getAllStoriesWithToken(token: String): Flow<PagingData<StoryResponse>> =
        if (token.isEmpty())
            throw IllegalArgumentException()
        else {
            flowOf(PagingData.from(DataDummy.generateStories()))
        }


    override suspend fun getStoriesWithTokenAndLocation(token: String): List<StoryResponse> =
        if (token.isEmpty())
            throw IllegalArgumentException()
        else
            DataDummy.generateStories()

}