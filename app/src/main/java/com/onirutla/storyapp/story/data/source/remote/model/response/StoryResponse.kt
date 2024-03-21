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