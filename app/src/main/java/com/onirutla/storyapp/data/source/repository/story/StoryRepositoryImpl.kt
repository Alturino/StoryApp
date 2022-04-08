package com.onirutla.storyapp.data.source.repository.story

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.StoryPagingDataSource
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import com.onirutla.storyapp.util.Constants.NETWORK_LOAD_SIZE
import com.onirutla.storyapp.util.compressImage
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApiService: StoryApiService
) : StoryRepository {

    override suspend fun addNewStoryWithToken(
        description: String,
        image: File,
        token: String
    ): BaseResponse = try {
        val compressedImage = image.compressImage()
        val descriptionMultipart = MultipartBody.Part.createFormData("description", description)
        val imageRequestBody = compressedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultiPart = MultipartBody.Part.createFormData(
            "photo",
            compressedImage.name,
            imageRequestBody
        )
        val response = storyApiService.addNewStoryWithToken(
            descriptionMultipart,
            imageMultiPart,
            "Bearer $token"
        )
        if (response.isSuccessful) {
            Log.d("addStoryWithToken", "${response.body()}")
            response.body()!!
        } else {
            Log.d("addStoryWithToken", "${response.errorBody()}")
            BaseResponse()
        }
    } catch (e: Exception) {
        Log.d("addStoryWithToken", "$e", e)
        BaseResponse()
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