package com.onirutla.storyapp.data.user.service

import com.onirutla.storyapp.data.user.body.UserLoginBody
import com.onirutla.storyapp.data.user.body.UserRegisterBody
import com.onirutla.storyapp.data.user.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST(value = "/register")
    suspend fun registerUser(@Body registerBody: UserRegisterBody): Response<UserRegisterBody>

    @POST(value = "/login")
    suspend fun loginUser(@Body loginBody: UserLoginBody): Response<UserResponse>

}