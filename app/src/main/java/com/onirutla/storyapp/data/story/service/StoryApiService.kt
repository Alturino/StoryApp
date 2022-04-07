package com.onirutla.storyapp.data.story.service

import com.onirutla.storyapp.data.BaseResponse
import com.onirutla.storyapp.data.PageResponse
import com.onirutla.storyapp.data.story.response.StoryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface StoryApiService {

    @GET(value = "/stories")
    suspend fun getAllStoriesWithToken(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 1,
        @Header("Authorization") token: String
    ): Response<PageResponse<StoryResponse>>

    @Multipart
    @POST(value = "/stories")
    suspend fun addNewStoryWithToken(
        @Part photo: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<BaseResponse>

    @Multipart
    @POST(value = "/stories")
    suspend fun addNewStoryWithoutToken(
        @Part photo: MultipartBody.Part
    ): Response<BaseResponse>

}