package com.onirutla.storyapp.story.ui.story_map

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.onirutla.storyapp.story.domain.data.Story

data class StoryMapState(
    val bottomSheetState: Int = BottomSheetBehavior.STATE_EXPANDED,
    val stories: List<Story> = listOf(),
    val currentStory: Story = Story(),
    val currentStoryIndex: Int = 0,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)