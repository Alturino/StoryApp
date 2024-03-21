package com.onirutla.storyapp.auth.domain

import com.onirutla.storyapp.auth.data.source.remote.model.request.LoginRequest
import com.onirutla.storyapp.auth.data.source.remote.model.request.RegisterRequest
import com.onirutla.storyapp.auth.domain.data.Login
import com.onirutla.storyapp.core.domain.ResponseState

interface AuthRepository {
    suspend fun login(login: LoginRequest): ResponseState<Login>
    suspend fun register(register: RegisterRequest): ResponseState<Unit>
    suspend fun updateUserSession(login: Login)
}