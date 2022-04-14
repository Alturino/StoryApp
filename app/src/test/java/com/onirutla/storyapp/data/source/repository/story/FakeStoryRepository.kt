package com.onirutla.storyapp.data.source.repository.story

import androidx.paging.PagingData
import com.onirutla.storyapp.data.DataDummy
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MultipartBody

class FakeStoryRepository : StoryRepository {
    override suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): BaseResponse {
        if (token.isEmpty()) {
            return BaseResponse(error = true)
        }
        return BaseResponse(error = false)
    }

    override fun getAllStoriesWithToken(token: String): Flow<PagingData<StoryResponse>> {
        if (token.isEmpty())
            return flowOf(PagingData.empty())
        else {
            return flowOf(PagingData.from(DataDummy.generateStories()))
        }
    }
}