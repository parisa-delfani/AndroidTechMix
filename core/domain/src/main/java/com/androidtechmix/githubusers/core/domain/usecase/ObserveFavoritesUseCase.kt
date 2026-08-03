package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.domain.model.User
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> = repository.observeFavorites()
}
