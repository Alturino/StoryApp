package com.onirutla.storyapp.auth.ui.register

import com.onirutla.storyapp.core.domain.ResponseState

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val registerState: ResponseState<Unit> = ResponseState.Initial,
)
