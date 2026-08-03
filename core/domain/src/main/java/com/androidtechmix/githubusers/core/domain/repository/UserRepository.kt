package com.androidtechmix.githubusers.core.domain.repository

import androidx.paging.PagingData
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.model.User
import com.androidtechmix.githubusers.core.model.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun searchUsers(query: String): Flow<PagingData<User>>
    fun observeUserDetail(login: String): Flow<UserDetail?>
    suspend fun refreshUserDetail(login: String): AppResult<Unit>
    fun observeFavorites(): Flow<List<User>>
    suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit>
    fun observeIsFavorite(login: String): Flow<Boolean>
}
