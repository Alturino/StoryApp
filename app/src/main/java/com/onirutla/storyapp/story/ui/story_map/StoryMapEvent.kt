package com.onirutla.storyapp.story.ui.story_map

import com.google.android.gms.maps.model.Marker

sealed interface StoryMapEvent {
    data class OnBottomSheetStateChange(val value: Int) : StoryMapEvent
    data object OnStoryPrev : StoryMapEvent
    data object OnStoryNext : StoryMapEvent
    data class OnMarkerClick(val marker: Marker): StoryMapEvent
}