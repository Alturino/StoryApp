package com.onirutla.storyapp.story.data.source.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.onirutla.storyapp.core.domain.secrets.Secret.STORY_API_STARTING_INDEX
import com.onirutla.storyapp.core.source.local.entity.RemoteKeyEntity
import com.onirutla.storyapp.core.source.local.entity.StoryEntity
import com.onirutla.storyapp.story.data.source.remote.model.response.StoryResponse
import com.onirutla.storyapp.story.data.source.remote.model.response.toEntities
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val isOnline: Boolean,
    private inline val response: suspend (page: Int, pageSize: Int) -> List<StoryResponse>,
    private inline val transaction: suspend (items: List<StoryEntity>, loadType: LoadType, page: Int, endOfPaginationReached: Boolean) -> Unit,
    private inline val getRemoteKeyByStoryId: suspend (id: String) -> RemoteKeyEntity?,
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction = if (isOnline) {
        InitializeAction.LAUNCH_INITIAL_REFRESH
    } else {
        InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.nextKey?.minus(1) ?: STORY_API_STARTING_INDEX
                }

                LoadType.PREPEND -> {
                    val remoteKey = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKey?.prevKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null
                    )
                    prevKey
                }

                LoadType.APPEND -> {
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKey?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null
                    )
                    nextKey
                }
            }

            val response = response(page, state.config.pageSize)
            val entities = response.toEntities()

            val endOfPaginationReached = response.isEmpty()
            Timber.d("endOfPaginationReached: $endOfPaginationReached")

            transaction(entities, loadType, page, endOfPaginationReached)

            MediatorResult.Success(endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: UnknownHostException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { getRemoteKeyByStoryId(it.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                getRemoteKeyByStoryId(it)
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                getRemoteKeyByStoryId(it)
            }
        }
    }
}
