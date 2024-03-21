package com.onirutla.storyapp.story.domain.data

data class Story(
    val createdAt: String = "",
    val description: String = "",
    val id: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val name: String = "",
    val photoUrl: String = "",
)
