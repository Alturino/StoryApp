package com.onirutla.storyapp.ui.map

import DataDummy
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.onirutla.storyapp.MainCoroutineRule
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class MapStoryViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var storyRepository: StoryRepository
    private lateinit var userRepository: UserRepository

    private lateinit var mapStoryViewModel: MapStoryViewModel

    private val token = "token"

    @Before
    fun setUp() = runBlockingTest {
        storyRepository = mock(StoryRepository::class.java)
        userRepository = mock(UserRepository::class.java)
    }

    @Test
    fun `given empty stories should return with empty data`() =
        mainCoroutineRule.runBlockingTest {
            val expected = emptyList<StoryResponse>()
            `when`(userRepository.getUserToken()).thenReturn(token)
            `when`(storyRepository.getStoriesWithTokenAndLocation(token)).thenReturn(expected)
            mapStoryViewModel = MapStoryViewModel(storyRepository, userRepository)

            val actual = mapStoryViewModel.stories.getOrAwaitValue()

            assertEquals(expected, actual)

            verify(userRepository).getUserToken()
            verify(storyRepository).getStoriesWithTokenAndLocation(token)
        }

    @Test
    fun `given stories should return with stories`() = mainCoroutineRule.runBlockingTest {
        val expected = DataDummy.generateStories()
        `when`(userRepository.getUserToken()).thenReturn(token)
        `when`(storyRepository.getStoriesWithTokenAndLocation(token)).thenReturn(expected)
        mapStoryViewModel = MapStoryViewModel(storyRepository, userRepository)

        val actual = mapStoryViewModel.stories.getOrAwaitValue()

        assertEquals(expected, actual)

        verify(userRepository).getUserToken()
        verify(storyRepository).getStoriesWithTokenAndLocation(token)
    }

}