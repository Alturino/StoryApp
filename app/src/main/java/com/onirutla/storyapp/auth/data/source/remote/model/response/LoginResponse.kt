package com.onirutla.storyapp.auth.data.source.remote.model.response

import com.onirutla.storyapp.auth.domain.data.Login
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseData(
    val name: String = "",
    val token: String = "",
    val userId: String = "",
)

@Serializable
data class LoginResponse(
    val error: Boolean? = false,
    @SerialName("loginResult")
    val loginData: LoginResponseData? = LoginResponseData(),
    val message: String? = "",
)

fun LoginResponse.toLogin() = Login(
    name = loginData?.name.orEmpty(),
    token = loginData?.token.orEmpty(),
    userId = loginData?.userId.orEmpty(),
)