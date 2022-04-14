package com.onirutla.storyapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stories = MutableStateFlow<PagingData<StoryResponse>>(PagingData.empty())
    val stories = _stories.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            userRepository.userToken.collect { token ->
                storyRepository.getAllStoriesWithToken(token).collect {
                    _stories.value = it
                }
            }
        }
    }

    fun getNewestStory() {
        viewModelScope.launch {
            userRepository.userToken.collect { token ->
                storyRepository.getAllStoriesWithToken(token).collect {
                    _stories.value = it
                }
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }
}