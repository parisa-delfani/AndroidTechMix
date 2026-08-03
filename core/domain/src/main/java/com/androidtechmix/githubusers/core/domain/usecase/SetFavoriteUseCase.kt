package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import javax.inject.Inject

class SetFavoriteUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(login: String, favorite: Boolean): AppResult<Unit> =
        repository.setFavorite(login.trim(), favorite)
}
