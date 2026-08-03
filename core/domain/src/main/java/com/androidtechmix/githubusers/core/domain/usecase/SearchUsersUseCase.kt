package com.androidtechmix.githubusers.core.domain.usecase

import androidx.paging.PagingData
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(query: String): Flow<PagingData<User>> {
        val trimmed = query.trim()
        require(trimmed.isNotEmpty()) { "Search query must not be empty" }
        return repository.searchUsers(trimmed)
    }
}
