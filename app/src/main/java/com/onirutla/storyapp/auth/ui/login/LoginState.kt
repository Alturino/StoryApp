package com.onirutla.storyapp.auth.ui.login

import com.onirutla.storyapp.auth.domain.data.Login
import com.onirutla.storyapp.core.domain.ResponseState

data class LoginState(
    val email: String = "",
    val password: String = "",
    val loginState: ResponseState<Login> = ResponseState.Initial,
)
