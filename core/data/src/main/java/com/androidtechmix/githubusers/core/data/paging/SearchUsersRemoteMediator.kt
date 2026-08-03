package com.androidtechmix.githubusers.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.androidtechmix.githubusers.core.common.result.AppException
import com.androidtechmix.githubusers.core.data.mapper.toEntity
import com.androidtechmix.githubusers.core.database.GitHubDatabase
import com.androidtechmix.githubusers.core.database.entity.SearchRemoteKeyEntity
import com.androidtechmix.githubusers.core.database.entity.SearchResultEntity
import com.androidtechmix.githubusers.core.database.entity.UserEntity
import com.androidtechmix.githubusers.core.network.api.GitHubApi
import com.androidtechmix.githubusers.core.network.error.mapToAppError

@OptIn(ExperimentalPagingApi::class)
class SearchUsersRemoteMediator(
    private val query: String,
    private val api: GitHubApi,
    private val database: GitHubDatabase,
) : RemoteMediator<Int, UserEntity>() {

    private val searchDao = database.searchDao()
    private val userDao = database.userDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = searchDao.remoteKey(query)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = api.searchUsers(
                query = query,
                page = page,
                perPage = state.config.pageSize,
            )

            val users = response.items
            val endOfPaginationReached = users.isEmpty() ||
                users.size < state.config.pageSize ||
                page * state.config.pageSize >= response.totalCount

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    searchDao.clearSearch(query)
                }

                val startIndex = ((page - 1) * state.config.pageSize)
                userDao.upsertUsers(users.map { it.toEntity() })
                searchDao.insertSearchResults(
                    users.mapIndexed { index, user ->
                        SearchResultEntity(
                            query = query,
                            userId = user.id,
                            pageIndex = page,
                            itemIndex = startIndex + index,
                        )
                    },
                )
                searchDao.upsertRemoteKey(
                    SearchRemoteKeyEntity(
                        query = query,
                        prevPage = if (page == 1) null else page - 1,
                        nextPage = if (endOfPaginationReached) null else page + 1,
                    ),
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (t: Throwable) {
            MediatorResult.Error(AppException(t.mapToAppError(), t))
        }
    }
}
