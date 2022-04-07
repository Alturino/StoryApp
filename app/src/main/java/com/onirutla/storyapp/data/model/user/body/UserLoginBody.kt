package com.onirutla.storyapp.data.model.user.body

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserLoginBody(
    @Json(name = "email")
    val email: String? = "",
    @Json(name = "password")
    val password: String? = "",
)
