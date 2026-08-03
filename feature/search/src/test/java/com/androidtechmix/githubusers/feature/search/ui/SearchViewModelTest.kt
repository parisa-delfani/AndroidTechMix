package com.androidtechmix.githubusers.feature.search.ui

import androidx.paging.PagingData
import app.cash.turbine.test
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.model.User
import com.androidtechmix.githubusers.core.model.UserDetail
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.domain.usecase.ObserveFavoritesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SearchUsersUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SetFavoriteUseCase
import com.androidtechmix.githubusers.core.testing.MainDispatcherRule
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEffect
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query updates immediately and submitted query after debounce`() = runTest {
        val viewModel = SearchViewModel(
            searchUsers = SearchUsersUseCase(FakeRepo()),
            setFavorite = SetFavoriteUseCase(FakeRepo()),
            observeFavorites = ObserveFavoritesUseCase(FakeRepo()),
        )

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onEvent(SearchUiEvent.QueryChanged("octocat"))
            advanceUntilIdle()
            assertThat(awaitItem().query).isEqualTo("octocat")

            advanceTimeBy(500)
            advanceUntilIdle()
            assertThat(expectMostRecentItem().submittedQuery).isEqualTo("octocat")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `open user emits navigation effect`() = runTest {
        val viewModel = SearchViewModel(
            searchUsers = SearchUsersUseCase(FakeRepo()),
            setFavorite = SetFavoriteUseCase(FakeRepo()),
            observeFavorites = ObserveFavoritesUseCase(FakeRepo()),
        )

        viewModel.effects.test {
            viewModel.onEvent(SearchUiEvent.OpenUser("octocat"))
            assertThat(awaitItem()).isEqualTo(SearchUiEffect.NavigateToDetail("octocat"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeRepo : UserRepository {
    override fun searchUsers(query: String): Flow<PagingData<User>> = flowOf(PagingData.empty())
    override fun observeUserDetail(login: String): Flow<UserDetail?> = flowOf(null)
    override suspend fun refreshUserDetail(login: String): AppResult<Unit> = AppResult.Success(Unit)
    override fun observeFavorites(): Flow<List<User>> = flowOf(emptyList())
    override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> =
        AppResult.Success(Unit)
    override fun observeIsFavorite(login: String): Flow<Boolean> = flowOf(false)
}
