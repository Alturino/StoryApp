package com.onirutla.storyapp.data.source.repository.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.PageResponse
import com.onirutla.storyapp.data.model.StoryPagingDataSource
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import com.onirutla.storyapp.util.Constants.NETWORK_LOAD_SIZE
import com.onirutla.storyapp.util.EspressoIdlingResource
import com.onirutla.storyapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApiService: StoryApiService
) : StoryRepository {

    override suspend fun addNewStoryWithToken(
        description: MultipartBody.Part,
        image: MultipartBody.Part,
        token: String
    ): BaseResponse = try {
        EspressoIdlingResource.increment()
        val response = storyApiService.addNewStoryWithToken(description, image, token)
        EspressoIdlingResource.decrement()
        response
    } catch (e: Exception) {
        BaseResponse(error = true, e.localizedMessage)
    }

    override suspend fun getStoriesWithTokenAndLocation(token: String): PageResponse<StoryResponse> =
        try {
            val response = storyApiService.getStoriesWithTokenAndLocation(token = "Bearer $token")
            response
        } catch (e: Exception) {
            PageResponse(error = true, emptyList(), message = e.localizedMessage)
        }

    override fun getAllStoriesWithToken(token: String): Flow<PagingData<StoryResponse>> = Pager(
        config = PagingConfig(pageSize = NETWORK_LOAD_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            StoryPagingDataSource(
                apiService = storyApiService,
                token = "Bearer $token"
            )
        }
    ).flow
}