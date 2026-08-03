package com.androidtechmix.githubusers.feature.favorites.ui

import androidx.paging.PagingData
import app.cash.turbine.test
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.model.User
import com.androidtechmix.githubusers.core.domain.model.UserDetail
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.domain.usecase.ObserveFavoritesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SetFavoriteUseCase
import com.androidtechmix.githubusers.core.testing.MainDispatcherRule
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `exposes favorites from use case`() = runTest {
        val user = User(
            id = 1,
            login = "octocat",
            avatarUrl = "https://example.com/a.png",
            htmlUrl = "https://github.com/octocat",
            type = "User",
            isFavorite = true,
        )
        val repo = object : UserRepository by EmptyUserRepository() {
            override fun observeFavorites(): Flow<List<User>> = flowOf(listOf(user))
        }
        val viewModel = FavoritesViewModel(
            observeFavorites = ObserveFavoritesUseCase(repo),
            setFavorite = SetFavoriteUseCase(repo),
        )

        viewModel.uiState.test {
            skipItems(1)
            assertThat(awaitItem().favorites).containsExactly(user)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove favorite emits navigation free success path`() = runTest {
        val favorites = MutableStateFlow(
            listOf(
                User(
                    id = 1,
                    login = "octocat",
                    avatarUrl = "url",
                    htmlUrl = "html",
                    type = "User",
                    isFavorite = true,
                ),
            ),
        )
        val repo = object : UserRepository by EmptyUserRepository() {
            override fun observeFavorites(): Flow<List<User>> = favorites
            override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> {
                favorites.value = emptyList()
                return AppResult.Success(Unit)
            }
        }
        val viewModel = FavoritesViewModel(
            observeFavorites = ObserveFavoritesUseCase(repo),
            setFavorite = SetFavoriteUseCase(repo),
        )

        viewModel.onEvent(
            FavoritesUiEvent.RemoveFavorite(favorites.value.first()),
        )
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.favorites).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private open class EmptyUserRepository : UserRepository {
    override fun searchUsers(query: String) = flowOf(PagingData.empty<User>())
    override fun observeUserDetail(login: String): Flow<UserDetail?> = flowOf(null)
    override suspend fun refreshUserDetail(login: String): AppResult<Unit> = AppResult.Success(Unit)
    override fun observeFavorites(): Flow<List<User>> = flowOf(emptyList())
    override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> =
        AppResult.Success(Unit)
    override fun observeIsFavorite(login: String): Flow<Boolean> = flowOf(false)
}
