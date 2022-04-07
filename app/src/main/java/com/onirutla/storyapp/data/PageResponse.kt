package com.onirutla.storyapp.data


import com.squareup.moshi.Json

data class PageResponse<T>(
    @Json(name = "error")
    val error: Boolean? = false,
    @Json(name = "listStory")
    val listStory: List<T>? = listOf(),
    @Json(name = "message")
    val message: String? = ""
)