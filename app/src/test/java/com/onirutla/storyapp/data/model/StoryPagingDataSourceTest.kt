package com.onirutla.storyapp.data.model

import androidx.paging.PagingSource
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import com.onirutla.storyapp.util.Constants.NETWORK_LOAD_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class StoryPagingDataSourceTest {

    private lateinit var storyApiService: StoryApiService
    private lateinit var storyPagingDataSource: StoryPagingDataSource

    private val page = 1
    private val size = NETWORK_LOAD_SIZE
    private val token = "token"

    @Before
    fun setUp() {
        storyApiService = mock(StoryApiService::class.java)
        storyPagingDataSource = StoryPagingDataSource(storyApiService, token)
    }

    @Test
    fun loadReturnsPageWhenOnSuccessfulLoadOfItemKeyedData() = runTest {
        `when`(storyApiService.getAllStoriesWithToken(page, size, token)).thenReturn(
            PageResponse(
                listStory = listOf(
                    StoryResponse()
                )
            )
        )

        assertEquals(
            PagingSource.LoadResult.Page(
                data = listOf(StoryResponse()),
                prevKey = null,
                nextKey = 2
            ),
            storyPagingDataSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }


}