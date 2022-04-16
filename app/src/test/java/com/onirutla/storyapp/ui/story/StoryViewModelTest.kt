package com.onirutla.storyapp.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onirutla.storyapp.MainCoroutineRule
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class StoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var storyRepository: StoryRepository
    private lateinit var userRepository: UserRepository
    private lateinit var storyViewModel: StoryViewModel

    private val token = "token"

    @Before
    fun setUp() {
        storyRepository = mock(StoryRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        storyViewModel = StoryViewModel(storyRepository, userRepository)
    }

    @Test
    fun `given PagingData getAllStoriesWithToken shouldn't be null`() = mainCoroutineRule.runBlockingTest {
        val expected =
            MutableStateFlow(PagingData.empty<StoryResponse>()).cachedIn(storyViewModel.viewModelScope)
        `when`(userRepository.userToken).thenReturn(flowOf(token))
        `when`(storyRepository.getAllStoriesWithToken(token)).thenReturn(expected)

        val actual = storyViewModel.stories

        assertNotNull(actual)
    }

}