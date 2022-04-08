package com.onirutla.storyapp.data.source.api_service

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.PageResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface StoryApiService {

    @GET(value = "stories")
    suspend fun getAllStoriesWithToken(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 1,
        @Header("Authorization") token: String
    ): Response<PageResponse<StoryResponse>>

    @Multipart
    @POST(value = "stories")
    suspend fun addNewStoryWithToken(
        @Part description: MultipartBody.Part,
        @Part photo: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<BaseResponse>

    @Multipart
    @POST(value = "stories")
    suspend fun addNewStoryWithoutToken(
        @PartMap requestsBody: Map<String, RequestBody>,
        @Part photo: MultipartBody.Part
    ): Response<BaseResponse>

}