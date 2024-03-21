package com.onirutla.storyapp.core.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey
    val storyId: String,
    val prevKey: Int? = null,
    val nextKey: Int? = null,
)
