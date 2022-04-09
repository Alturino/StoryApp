package com.onirutla.storyapp.data.source.repository.user

import android.util.Log
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import com.onirutla.storyapp.data.source.DataStoreManager
import com.onirutla.storyapp.data.source.api_service.UserApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val dataStoreManager: DataStoreManager
) : UserRepository {

    private suspend fun addTokenToPreferences(loginToken: String) {
        dataStoreManager.updateLoginToken(loginToken)
    }

    override suspend fun registerUser(registerBody: UserRegisterBody): BaseResponse =
        try {
            val response = userApiService.registerUser(registerBody)
            if (response.isSuccessful) {
                Log.d("userRepository", "${response.body()}")
                response.body()!!
            } else {
                Log.d("userRepository", "${response.errorBody()}")
                BaseResponse(error = true, response.message())
            }
        } catch (e: Exception) {
            Log.d("userRepository", "$e", e)
            BaseResponse(error = true, e.localizedMessage)
        }

    override suspend fun loginUser(loginBody: UserLoginBody): LoginResponse = try {
        val response = userApiService.loginUser(loginBody)
        if (response.isSuccessful) {
            Log.d("userRepository", "${response.body()}")
            response.body()?.loginData?.token?.let { addTokenToPreferences(it) }
            response.body()!!
        } else {
            Log.d("userRepository", "${response.errorBody()}")
            LoginResponse()
        }
    } catch (e: Exception) {
        Log.d("userRepository", "$e", e)
        LoginResponse()
    }

    override val userToken: Flow<String> get() = dataStoreManager.preferenceLoginToken
}