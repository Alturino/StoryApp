package com.onirutla.storyapp.data.model.user.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "error")
    val error: Boolean? = true,
    @Json(name = "loginResult")
    val loginData: LoginData? = LoginData(),
    @Json(name = "message")
    val message: String? = ""
)