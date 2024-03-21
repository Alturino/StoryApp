package com.onirutla.storyapp.core.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onirutla.storyapp.core.util.orZero
import com.onirutla.storyapp.story.domain.data.Story
import java.time.Instant

@Entity(tableName = "stories")
data class StoryEntity(
    @ColumnInfo("created_at")
    val createdAt: Instant,
    val description: String,
    @PrimaryKey
    val id: String = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
    @ColumnInfo(index = true)
    val name: String = "",
    @ColumnInfo(name = "photo_url", index = true)
    val photoUrl: String = "",
)

fun StoryEntity.toStory() = Story(
    createdAt = createdAt.toString(),
    description = description,
    id = id,
    lat = lat.orZero(),
    lon = lon.orZero(),
    name = name,
    photoUrl = photoUrl,
)