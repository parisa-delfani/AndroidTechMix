package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import javax.inject.Inject

class RefreshUserDetailUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(login: String): AppResult<Unit> =
        repository.refreshUserDetail(login.trim())
}
