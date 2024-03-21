package com.onirutla.storyapp.story.ui.add_story

import android.net.Uri

sealed interface AddStoryEvent {
    data class OnDescriptionChange(val value: String) : AddStoryEvent
    data class OnUriChange(val value: Uri) : AddStoryEvent
    data class OnLatChange(val value: Double) : AddStoryEvent
    data class OnLonChange(val value: Double) : AddStoryEvent
}