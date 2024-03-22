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

package com.onirutla.storyapp.story.data.source.remote.model.response

import com.onirutla.storyapp.core.source.local.entity.StoryEntity
import com.onirutla.storyapp.core.util.orZero
import com.onirutla.storyapp.story.domain.data.Story
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class StoryResponse(
    val createdAt: String? = "",
    val description: String? = "",
    val id: String? = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
    val name: String? = "",
    val photoUrl: String? = "",
)

fun StoryResponse.toStory() = Story(
    createdAt = createdAt.orEmpty(),
    description = description.orEmpty(),
    id = id.orEmpty(),
    lat = lat.orZero(),
    lon = lon.orZero(),
    name = name.orEmpty(),
    photoUrl = photoUrl.orEmpty(),
)

fun List<StoryResponse>.toStories() = map { it.toStory() }

fun StoryResponse.toEntity() = StoryEntity(
    createdAt = if (createdAt.isNullOrEmpty()) Instant.now() else Instant.parse(createdAt),
    description = description.orEmpty(),
    id = id.orEmpty(),
    lat = lat.orZero(),
    lon = lon.orZero(),
    name = name.orEmpty(),
    photoUrl = photoUrl.orEmpty(),
)

fun List<StoryResponse>.toEntities() = map { it.toEntity() }