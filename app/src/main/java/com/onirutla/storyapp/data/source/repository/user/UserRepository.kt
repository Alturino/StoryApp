package com.onirutla.storyapp.data.source.repository.user

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface UserRepository {

    suspend fun registerUser(registerBody: UserRegisterBody): BaseResponse

    suspend fun loginUser(loginBody: UserLoginBody): LoginResponse

    suspend fun logoutUser()

    val userToken: Flow<String>
}