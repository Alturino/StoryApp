package com.onirutla.storyapp.story.data.source.remote.model.request

import java.io.File

data class StoryRequest(
    val description: String,
    val photo: File,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)
