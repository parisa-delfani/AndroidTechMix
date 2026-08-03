package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.model.UserDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserDetailUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(login: String): Flow<UserDetail?> =
        repository.observeUserDetail(login.trim())
}
