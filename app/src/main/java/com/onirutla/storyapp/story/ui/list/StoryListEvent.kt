package com.onirutla.storyapp.story.ui.list

import com.onirutla.storyapp.story.domain.data.StoryFilterType
import com.onirutla.storyapp.story.domain.data.StorySortType

sealed interface StoryListEvent {
    data class OnOnlineChange(val value: Boolean) : StoryListEvent
    data class OnFilterChange(val value: StoryFilterType) : StoryListEvent
    data class OnSortChange(val value: StorySortType) : StoryListEvent
}