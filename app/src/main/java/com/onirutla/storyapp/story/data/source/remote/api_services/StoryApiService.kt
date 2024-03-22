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

package com.onirutla.storyapp.story.data.source.remote.api_services

import arrow.core.Either
import com.onirutla.storyapp.core.source.remote.model.BaseResponse
import com.onirutla.storyapp.story.data.source.remote.model.request.StoryRequest
import com.onirutla.storyapp.story.data.source.remote.model.response.StoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryApiService @Inject constructor(
    private val ktorClient: HttpClient,
) {

    suspend fun getStories(
        page: Int,
        size: Int,
        token: String,
        withLocation: Boolean = false,
    ): Either<Throwable, BaseResponse<StoryResponse>> {
        val response = Either.catch {
            ktorClient.get(urlString = "stories") {
                url {
                    parameters.append(name = "page", value = "$page")
                    parameters.append(name = "size", value = "$size")
                    parameters.append(name = "location", value = if (withLocation) "1" else "0")
                }
                headers {
                    append(name = HttpHeaders.Authorization, value = "Bearer $token")
                }
            }.body<BaseResponse<StoryResponse>>()
        }
        return response
    }

    suspend fun getStoryById(
        id: String,
        token: String,
    ): Either<Throwable, BaseResponse<StoryResponse>> {
        val response = Either.catch {
            ktorClient.get(urlString = "stories") {
                url { appendPathSegments(id) }
                headers { append(name = HttpHeaders.Authorization, value = "Bearer $token") }
            }.body<BaseResponse<StoryResponse>>()
        }
        return response
    }

    suspend fun uploadStory(
        story: StoryRequest,
        token: String,
        progress: suspend (sent: Long, length: Long) -> Unit,
    ): Either<Throwable, BaseResponse<Unit>> {
        val response = Either.catch {
            ktorClient.post(urlString = "stories") {
                contentType(ContentType.MultiPart.FormData)
                headers {
                    append(name = HttpHeaders.Authorization, value = "Bearer $token")
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(key = "description", value = story.description)
                            append(key = "lat", value = story.lat)
                            append(key = "lon", value = story.lon)
                            append(
                                key = "photo",
                                value = story.photo.readBytes(),
                                headers = Headers.build {
                                    append(
                                        name = HttpHeaders.ContentType,
                                        value = ContentType.Image.JPEG.contentType,
                                    )
                                    append(
                                        name = HttpHeaders.ContentDisposition,
                                        value = "filename=${story.photo.name}"
                                    )
                                }
                            )
                        }
                    )
                )
                onUpload(progress)
            }.body<BaseResponse<Unit>>()
        }
        return response
    }
}