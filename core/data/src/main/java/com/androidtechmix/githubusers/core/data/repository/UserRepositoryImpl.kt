package com.androidtechmix.githubusers.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.common.util.Constants
import com.androidtechmix.githubusers.core.data.mapper.toDomain
import com.androidtechmix.githubusers.core.data.mapper.toEntity
import com.androidtechmix.githubusers.core.data.mapper.toFavoriteEntity
import com.androidtechmix.githubusers.core.data.paging.SearchUsersRemoteMediator
import com.androidtechmix.githubusers.core.database.GitHubDatabase
import com.androidtechmix.githubusers.core.database.dao.FavoriteDao
import com.androidtechmix.githubusers.core.database.dao.UserDao
import com.androidtechmix.githubusers.core.database.dao.UserDetailDao
import com.androidtechmix.githubusers.core.domain.model.User
import com.androidtechmix.githubusers.core.domain.model.UserDetail
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.network.api.GitHubApi
import com.androidtechmix.githubusers.core.network.error.mapToAppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: GitHubApi,
    private val database: GitHubDatabase,
    private val userDao: UserDao,
    private val userDetailDao: UserDetailDao,
    private val favoriteDao: FavoriteDao,
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun searchUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.SEARCH_PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5,
                initialLoadSize = Constants.SEARCH_PAGE_SIZE,
            ),
            remoteMediator = SearchUsersRemoteMediator(
                query = query,
                api = api,
                database = database,
            ),
            pagingSourceFactory = { database.searchDao().pagingSource(query) },
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain(isFavorite = false) }
        }
    }

    override fun observeUserDetail(login: String): Flow<UserDetail?> {
        return combine(
            userDetailDao.observeDetail(login),
            userDetailDao.observeRepos(login),
            favoriteDao.observeIsFavorite(login),
        ) { detail, repos, isFavorite ->
            detail?.toDomain(
                isFavorite = isFavorite,
                repositories = repos.map { it.toDomain() },
            )
        }
    }

    override suspend fun refreshUserDetail(login: String): AppResult<Unit> {
        return try {
            val detailDto = api.getUser(login)
            val reposDto = api.getUserRepos(
                username = login,
                perPage = Constants.TOP_REPOS_LIMIT,
            )
            val detailEntity = detailDto.toEntity(updatedAtLocal = System.currentTimeMillis())
            val repoEntities = reposDto.map { it.toEntity(ownerLogin = login) }
            userDetailDao.upsertDetailWithRepos(detailEntity, repoEntities)
            userDao.upsertUsers(
                listOf(
                    com.androidtechmix.githubusers.core.database.entity.UserEntity(
                        id = detailDto.id,
                        login = detailDto.login,
                        avatarUrl = detailDto.avatarUrl,
                        htmlUrl = detailDto.htmlUrl,
                        type = "User",
                    ),
                ),
            )
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(t.mapToAppError())
        }
    }

    override fun observeFavorites(): Flow<List<User>> =
        favoriteDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> {
        return try {
            if (favorite) {
                val detail = userDetailDao.getDetail(login)
                val user = userDao.getUserByLogin(login)
                val entity = when {
                    detail != null -> detail.toFavoriteEntity(System.currentTimeMillis())
                    user != null -> user.toFavoriteEntity(System.currentTimeMillis())
                    else -> return AppResult.Error(AppError.NotFound)
                }
                favoriteDao.upsert(entity)
            } else {
                favoriteDao.delete(login)
            }
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(t.mapToAppError())
        }
    }

    override fun observeIsFavorite(login: String): Flow<Boolean> =
        favoriteDao.observeIsFavorite(login)
}
