package com.onirutla.storyapp.story.ui.add_story

import android.net.Uri

data class AddStoryState(
    val description: String = "",
    val photoUri: Uri = Uri.EMPTY,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)
