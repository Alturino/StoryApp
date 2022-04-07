package com.onirutla.storyapp.data.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import com.onirutla.storyapp.util.Constants.NETWORK_LOAD_SIZE
import com.onirutla.storyapp.util.Constants.STORY_API_STARTING_INDEX
import okio.IOException
import retrofit2.HttpException

class StoryPagingDataSource(
    private val apiService: StoryApiService,
    private val token: String
) : PagingSource<Int, StoryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> = try {
        val position = params.key ?: STORY_API_STARTING_INDEX
        val response = apiService.getAllStoriesWithToken(
            page = position,
            size = NETWORK_LOAD_SIZE,
            token = token
        )
        val stories = response.body()!!.listStory
        val nextKey = if (stories.isNullOrEmpty()) null else position + 1
        LoadResult.Page(
            data = stories.orEmpty(),
            prevKey = if (position == STORY_API_STARTING_INDEX) null else position - 1,
            nextKey = nextKey
        )
    } catch (exception: IOException) {
        LoadResult.Error(exception)
    } catch (exception: HttpException) {
        LoadResult.Error(exception)
    }
}