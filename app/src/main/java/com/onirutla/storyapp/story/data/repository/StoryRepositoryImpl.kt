package com.onirutla.storyapp.story.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import arrow.core.Either
import arrow.core.getOrElse
import com.onirutla.storyapp.core.domain.ResponseState
import com.onirutla.storyapp.core.source.local.db.StoryAppDB
import com.onirutla.storyapp.core.source.local.entity.RemoteKeyEntity
import com.onirutla.storyapp.core.source.local.entity.toStory
import com.onirutla.storyapp.core.source.remote.model.BaseResponse
import com.onirutla.storyapp.story.data.source.remote.api_services.StoryApiService
import com.onirutla.storyapp.story.data.source.remote.mediator.StoryRemoteMediator
import com.onirutla.storyapp.story.data.source.remote.model.request.StoryRequest
import com.onirutla.storyapp.story.data.source.remote.model.response.toStories
import com.onirutla.storyapp.story.data.source.remote.model.response.toStory
import com.onirutla.storyapp.story.domain.data.Story
import com.onirutla.storyapp.story.domain.data.StoryFilterType
import com.onirutla.storyapp.story.domain.data.StorySortType
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val apiService: StoryApiService,
    private val db: StoryAppDB,
) : StoryRepository {

    override fun getStoriesFlow(
        token: String,
    ): Flow<ResponseState<List<Story>>> = flow {
        val response = getStories(token = token)
        emit(response)
    }.onEach { Timber.i(it.toString()) }
        .onStart { ResponseState.Loading }
        .catch { Timber.e(it) }
        .retryWhen { _, attempt -> attempt < 3 }

    override suspend fun getStories(
        page: Int,
        size: Int,
        token: String,
        withLocation: Boolean,
    ): ResponseState<List<Story>> = apiService.getStories(
        page = page,
        size = size,
        token = token,
        withLocation = withLocation,
    ).map {
        if (it.error == true || it.listStory.isNullOrEmpty()) {
            ResponseState.Error(it.message.orEmpty())
        } else {
            ResponseState.Success(it.listStory.toStories())
        }
    }.mapLeft { ResponseState.Error(it.message.orEmpty()) }
        .getOrElse { it }

    override fun getStoryById(id: String, token: String): Flow<ResponseState<Story>> = flow {
        val response = apiService.getStoryById(id = id, token = token)
            .map { response ->
                if (response.story == null) {
                    ResponseState.Error("Story with id: $id not found")
                } else if (response.error == true) {
                    ResponseState.Error(response.message.orEmpty())
                } else {
                    ResponseState.Success(response.story.toStory())
                }
            }.mapLeft {
                ResponseState.Error(it.message.orEmpty())
            }.getOrElse { it }
        emit(response)
    }.onEach { Timber.i(it.toString()) }
        .onStart { ResponseState.Loading }
        .catch { Timber.e(it) }
        .retryWhen { _, attempt -> attempt < 3 }


    @OptIn(ExperimentalPagingApi::class)
    override fun getStoriesPaging(
        isOnline: Boolean,
        filter: StoryFilterType,
        sort: StorySortType,
        token: String,
        size: Int,
    ): Flow<PagingData<Story>> = Pager(
        config = PagingConfig(pageSize = size, enablePlaceholders = false),
        remoteMediator = StoryRemoteMediator(
            isOnline = isOnline,
            response = { page, pageSize ->
                apiService.getStories(
                    page,
                    pageSize,
                    token
                ).onLeft { Timber.e(it) }
                    .map { it.listStory.orEmpty() }
                    .getOrElse { throw it }
            },
            getRemoteKeyByStoryId = { db.remoteKeyDao.getRemoteKeyByStoryId(it) },
            transaction = { items, loadType, page, endOfPaginationReached ->
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        db.remoteKeyDao.clearRemoteKey()
                        db.storyDao.clearStories()
                    }

                    val prevKey = if (page == 1) null else page.minus(1)
                    val nextKey = if (endOfPaginationReached) null else page.plus(1)
                    val remoteKeys = items.map {
                        RemoteKeyEntity(
                            storyId = it.id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }

                    db.remoteKeyDao.upsertRemoteKey(*remoteKeys.toTypedArray())
                    db.storyDao.upsertStory(*items.toTypedArray())
                }
            },
        ),
        pagingSourceFactory = {
            val query = buildString {
                append("select * from stories ")
                when (filter) {
                    StoryFilterType.All -> {}
                    StoryFilterType.WithLocation -> append("where (lon != null or lon != 0.0) and (lat != null or lon != 0.0) ")
                    StoryFilterType.WithoutLocation -> append("where (lon == null or lon == 0.0) and (lat == null or lat == 0.0) ")
                }
                when (sort) {
                    StorySortType.NameAscending -> append("order by name asc")
                    StorySortType.NameDescending -> append("order by name desc")
                    StorySortType.CreatedAtAscending -> append("order by created_at asc")
                    StorySortType.CreatedAtDescending -> append("order by created_at desc")
                }
            }
            db.storyDao.getStoriesPaging(SimpleSQLiteQuery(query))
        }
    ).flow.map { pagingData ->
        pagingData.map { it.toStory() }
    }

    override suspend fun uploadStory(
        request: StoryRequest,
        token: String,
        progress: suspend (sent: Long, length: Long) -> Unit,
    ): Either<Throwable, BaseResponse<Unit>> {
        return apiService.uploadStory(story = request, token = token, progress = progress)
    }


}