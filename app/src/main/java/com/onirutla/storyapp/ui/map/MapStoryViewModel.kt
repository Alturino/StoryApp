package com.onirutla.storyapp.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<StoryResponse>>()
    val stories: LiveData<List<StoryResponse>> get() = _stories

    init {
        viewModelScope.launch {
            val token = userRepository.getUserToken()
            _stories.value = storyRepository.getStoriesWithTokenAndLocation(token)
        }
    }

}