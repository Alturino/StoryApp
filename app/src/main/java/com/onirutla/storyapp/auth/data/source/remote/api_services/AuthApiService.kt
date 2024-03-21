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