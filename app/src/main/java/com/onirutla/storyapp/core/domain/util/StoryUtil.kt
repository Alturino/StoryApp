package com.onirutla.storyapp.core.domain.util

import com.onirutla.storyapp.core.source.local.entity.StoryEntity
import com.onirutla.storyapp.story.domain.data.Story
import java.time.Instant

fun Story.toEntity() = StoryEntity(
    createdAt = Instant.parse(createdAt),
    description = description,
    id = id,
    lat = lat,
    lon = lon,
    name = name,
    photoUrl = photoUrl,
)

fun List<Story>.toEntities() = map { it.toEntity() }