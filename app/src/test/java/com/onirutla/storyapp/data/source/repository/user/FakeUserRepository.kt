package com.onirutla.storyapp.data.source.repository.user

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.model.user.response.LoginData
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class FakeUserRepository : UserRepository {

    private val loginToken = MutableSharedFlow<String>(1)

    override suspend fun registerUser(registerBody: UserRegisterBody): BaseResponse {
        val (name, email, password) = registerBody
        return if (name.isNullOrEmpty() or email.isNullOrEmpty() or password.isNullOrEmpty())
            BaseResponse(true)
        else
            BaseResponse(error = false)
    }

    override suspend fun loginUser(loginBody: UserLoginBody): LoginResponse {
        val (email, password) = loginBody
        return if (email.isNullOrEmpty() or password.isNullOrEmpty())
            LoginResponse(error = true)
        else {
            val token = "token"
            loginToken.emit(token)
            LoginResponse(error = false, loginData = LoginData(token))
        }
    }

    override suspend fun logoutUser() {
        loginToken.emit("")
    }

    override val userToken: Flow<String>
        get() = loginToken

    override suspend fun getUserToken(): String = loginToken.first()

    override suspend fun setUserToken(token: String) {
        loginToken.emit(token)
    }
}