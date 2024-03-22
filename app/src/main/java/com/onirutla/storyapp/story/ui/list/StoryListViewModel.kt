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

package com.onirutla.storyapp.story.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.story.domain.data.Story
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoryListViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(StoryListState())
    val state: StateFlow<StoryListState> = _state.asStateFlow()

    init {
        sessionManager.userSessionFlow
            .filter { it.token.isNotBlank() or it.token.isNotEmpty() }
            .map { it.token }
            .onEach { token -> _state.update { it.copy(token = token) } }
            .launchIn(viewModelScope)
    }

    val stories: StateFlow<PagingData<Story>> = state
        .onEach { Timber.d("state: $it") }
        .flatMapLatest {
            repository.getStoriesPaging(
                isOnline = it.isOnline,
                token = it.token,
                filter = it.filter,
                sort = it.sort
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun onEvent(event: StoryListEvent) {
        when (event) {
            is StoryListEvent.OnFilterChange -> {
                _state.update { it.copy(filter = event.value) }
            }

            is StoryListEvent.OnOnlineChange -> {
                _state.update { it.copy(isOnline = event.value) }
            }

            is StoryListEvent.OnSortChange -> {
                _state.update { it.copy(sort = event.value) }
            }
        }
    }
}

