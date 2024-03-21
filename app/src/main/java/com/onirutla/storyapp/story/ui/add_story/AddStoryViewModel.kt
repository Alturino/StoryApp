package com.onirutla.storyapp.story.ui.add_story

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AddStoryState())
    val state: StateFlow<AddStoryState> = _state.asStateFlow()

    fun onEvent(event: AddStoryEvent) {
        when (event) {
            is AddStoryEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.value) }
            }

            is AddStoryEvent.OnLatChange -> {
                _state.update { it.copy(lat = event.value) }
            }

            is AddStoryEvent.OnLonChange -> {
                _state.update { it.copy(lon = event.value) }
            }

            is AddStoryEvent.OnUriChange -> {
                _state.update { it.copy(photoUri = event.value) }
            }
        }
    }

}

