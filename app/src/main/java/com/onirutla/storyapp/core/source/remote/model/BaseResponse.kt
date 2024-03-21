package com.onirutla.storyapp.core.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val error: Boolean? = false,
    val message: String? = "",
    val story: T? = null,
    val listStory: List<T>? = null,
)
