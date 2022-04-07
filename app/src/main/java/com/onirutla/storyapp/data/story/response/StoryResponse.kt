package com.onirutla.storyapp.data.story.response


import com.squareup.moshi.Json

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
)