package com.onirutla.storyapp.auth.data.source.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String? = "",
    val password: String? = "",
)
