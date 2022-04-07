package com.onirutla.storyapp.data.model.user.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "error")
    val error: Boolean? = false,
    @Json(name = "loginResult")
    val loginResponse: LoginResponse? = LoginResponse(),
    @Json(name = "message")
    val message: String? = ""
)