package com.onirutla.storyapp.story.ui.list

import com.onirutla.storyapp.story.domain.data.StoryFilterType
import com.onirutla.storyapp.story.domain.data.StorySortType

data class StoryListState(
    val isOnline: Boolean = false,
    val token: String = "",
    val filter: StoryFilterType = StoryFilterType.All,
    val sort: StorySortType = StorySortType.NameAscending,
)