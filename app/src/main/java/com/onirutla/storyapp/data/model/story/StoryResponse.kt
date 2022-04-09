package com.onirutla.storyapp.data.model.story

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class StoryResponse(
    @Json(name = "createdAt")
    val createdAt: String? = "",
    @Json(name = "description")
    val description: String? = "",
    @Json(name = "id")
    val id: String? = "",
    @Json(name = "lat")
    val lat: Double? = 0.0,
    @Json(name = "lon")
    val lon: Double? = 0.0,
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "photoUrl")
    val photoUrl: String? = ""
) : Parcelable