package com.onirutla.storyapp.data.source.api_service

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.PageResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.util.Constants.NETWORK_LOAD_SIZE
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface StoryApiService {

    @GET(value = "stories")
    suspend fun getAllStoriesWithToken(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = NETWORK_LOAD_SIZE,
        @Header("Authorization") token: String
    ): PageResponse<StoryResponse>

    @GET(value = "stories")
    suspend fun getStoriesWithTokenAndLatitude(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = NETWORK_LOAD_SIZE,
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): PageResponse<StoryResponse>

    @Multipart
    @POST(value = "stories")
    suspend fun addNewStoryWithToken(
        @Part description: MultipartBody.Part,
        @Part photo: MultipartBody.Part,
        @Header("Authorization") token: String
    ): BaseResponse

}