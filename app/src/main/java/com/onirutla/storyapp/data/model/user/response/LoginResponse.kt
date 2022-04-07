package com.onirutla.storyapp.data.model.user.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "token")
    val token: String? = "",
    @Json(name = "userId")
    val userId: String? = ""
)