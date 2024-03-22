/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.onirutla.storyapp.auth.data.repository

import arrow.core.getOrElse
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.auth.data.source.remote.api_services.AuthApiService
import com.onirutla.storyapp.auth.data.source.remote.model.request.LoginRequest
import com.onirutla.storyapp.auth.data.source.remote.model.request.RegisterRequest
import com.onirutla.storyapp.auth.data.source.remote.model.response.toLogin
import com.onirutla.storyapp.auth.domain.AuthRepository
import com.onirutla.storyapp.auth.domain.data.Login
import com.onirutla.storyapp.core.domain.ResponseState
import com.onirutla.storyapp.userSession
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userSessionManager: UserSessionManager,
) : AuthRepository {

    override suspend fun login(login: LoginRequest): ResponseState<Login> = apiService.login(login)
        .onLeft { Timber.e(it) }
        .mapLeft { ResponseState.Error(it.message.orEmpty()) }
        .map {
            Timber.d(it.toString())
            if (it.error == true) {
                ResponseState.Error(it.message.orEmpty())
            } else {
                val loginData = it.toLogin()
                ResponseState.Success(loginData)
            }
        }
        .getOrElse { it }

    override suspend fun register(
        register: RegisterRequest,
    ): ResponseState<Unit> = apiService.register(register)
        .onLeft { Timber.e(it) }
        .mapLeft { ResponseState.Error(it.message.orEmpty()) }
        .map {
            if (it.error == true) {
                ResponseState.Error(it.message.orEmpty())
            } else {
                ResponseState.Success(Unit)
            }
        }
        .getOrElse { it }

    override suspend fun updateUserSession(login: Login) {
        Timber.d("Updating User session with data: $login")
        userSessionManager.updateUserSession(
            userSession {
                name = login.name
                userId = login.userId
                token = login.token
            }
        )
        Timber.d("Updated user session with data")
    }
}