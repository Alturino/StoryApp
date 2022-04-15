package com.onirutla.storyapp.data.source.api_service

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST(value = "register")
    suspend fun registerUser(@Body registerBody: UserRegisterBody): BaseResponse

    @POST(value = "login")
    suspend fun loginUser(@Body loginBody: UserLoginBody): LoginResponse

}