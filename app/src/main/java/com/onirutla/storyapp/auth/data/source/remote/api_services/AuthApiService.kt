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

package com.onirutla.storyapp.auth.data.source.remote.api_services

import arrow.core.Either
import com.onirutla.storyapp.auth.data.source.remote.model.request.LoginRequest
import com.onirutla.storyapp.auth.data.source.remote.model.request.RegisterRequest
import com.onirutla.storyapp.auth.data.source.remote.model.response.LoginResponse
import com.onirutla.storyapp.core.source.remote.model.BaseResponse
import dagger.hilt.android.scopes.ViewModelScoped
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class AuthApiService @Inject constructor(
    private val ktorClient: HttpClient,
) {
    suspend fun register(registerRequest: RegisterRequest): Either<Throwable, BaseResponse<Unit>> {
        val response = Either.catch {
            val request = ktorClient.post(urlString = "register") {
                contentType(type = ContentType.Application.Json)
                setBody(registerRequest)
            }.body<BaseResponse<Unit>>()
            request
        }
        return response
    }

    suspend fun login(loginRequest: LoginRequest): Either<Throwable, LoginResponse> {
        val response = Either.catch {
            val request = ktorClient.post(urlString = "login") {
                contentType(type = ContentType.Application.Json)
                setBody(loginRequest)
            }

            val response = if (request.status != HttpStatusCode.OK) {
                request.body<LoginResponse>()
            } else {
                request.body<LoginResponse>()
            }

            Timber.d("$response")
            response
        }
        Timber.d("$response")
        return response
    }
}