/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.onirutla.storyapp.story.ui.story_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.core.domain.ResponseState
import com.onirutla.storyapp.story.domain.data.Story
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StoryMapViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(StoryMapState())
    val state: StateFlow<StoryMapState> = _state.asStateFlow()

    private val stories: StateFlow<ResponseState<List<Story>>> = userSessionManager.userSessionFlow
        .map { it.token }
        .filterNot { it.isEmpty() or it.isBlank() }
        .map {
            repository.getStories(
                page = 1,
                size = 100,
                token = it,
                withLocation = true
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResponseState.Initial
        )

    fun onEvent(event: StoryMapEvent) {
        when (event) {
            is StoryMapEvent.OnBottomSheetStateChange -> {
                _state.update { it.copy(bottomSheetState = event.value) }
            }

            is StoryMapEvent.OnStoryNext -> {
                _state.update { it.copy(currentStoryIndex = if (it.currentStoryIndex < it.stories.lastIndex) it.currentStoryIndex + 1 else it.currentStoryIndex) }
            }

            is StoryMapEvent.OnStoryPrev -> {
                _state.update { it.copy(currentStoryIndex = if (it.currentStoryIndex > 0) it.currentStoryIndex - 1 else it.currentStoryIndex) }
            }

            is StoryMapEvent.OnMarkerClick -> {
                _state.update {
                    val index = it.stories.indexOfFirst { story ->
                        story.lat == event.marker.position.latitude && story.lon == event.marker.position.longitude
                    }
                    it.copy(
                        currentStoryIndex = if (index == -1) it.currentStoryIndex else index
                    )
                }
            }
        }
    }

    init {
        _state.filterNot { it.stories.isEmpty() }
            .map { it.currentStoryIndex }
            .distinctUntilChanged()
            .onEach { index -> _state.update { it.copy(currentStory = it.stories[index]) } }
            .launchIn(viewModelScope)

        stories.onEach { stories ->
            stories.onError {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stories = listOf(),
                        isError = true,
                        errorMessage = message
                    )
                }
            }.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stories = data,
                        isError = false,
                        errorMessage = ""
                    )
                }
            }.onInitial {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stories = listOf(),
                        isError = false,
                        errorMessage = ""
                    )
                }
            }.onLoading {
                _state.update {
                    it.copy(
                        isLoading = true,
                        stories = listOf(),
                        isError = false,
                        errorMessage = ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}

