package com.onirutla.storyapp.auth.data.source.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
)
