package com.onirutla.storyapp.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.core.domain.ResponseState
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val isOnlineFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isLogin = userSessionManager.userSessionFlow.map { it.token }
        .combine(isOnlineFlow) { token, isOnline -> token to isOnline }
        .onEach { Timber.d("pair: $it") }
        .map { (token, isOnline) ->
            if (token.isEmpty() or token.isBlank()) {
                false
            } else if (isOnline) {
                val response = storyRepository.getStories(token = token)
                when (response) {
                    is ResponseState.Error -> false
                    ResponseState.Initial -> null
                    ResponseState.Loading -> null
                    is ResponseState.Success -> true
                }
            } else {
                true
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setOnline(isOnline: Boolean) {
        isOnlineFlow.update { isOnline }
    }
}
