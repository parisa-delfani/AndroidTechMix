package com.androidtechmix.githubusers.feature.compare.ui

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.androidtechmix.githubusers.core.domain.usecase.CompareProfilesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.ObserveUserDetailUseCase
import com.androidtechmix.githubusers.core.domain.usecase.RefreshUserDetailUseCase
import com.androidtechmix.githubusers.core.model.Repository
import com.androidtechmix.githubusers.core.model.User
import com.androidtechmix.githubusers.core.model.UserDetail
import com.androidtechmix.githubusers.core.testing.MainDispatcherRule
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEffect
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CompareViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads both profiles and shared languages`() = runTest {
        val viewModel = viewModel(
            left = detail("octocat", followers = 10, languages = listOf("Kotlin")),
            right = detail("torvalds", followers = 40, languages = listOf("Kotlin", "C")),
        )

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.left?.login).isEqualTo("octocat")
            assertThat(state.right?.login).isEqualTo("torvalds")
            assertThat(state.comparison?.shared?.map { it.language }).containsExactly("Kotlin")
            assertThat(state.comparison?.rightOnly?.map { it.language }).containsExactly("C")
            assertThat(state.isRefreshing).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `same user does not refresh`() = runTest {
        val repo = FakeCompareRepository()
        val viewModel = viewModel(repo = repo, leftLogin = "octocat", rightLogin = "Octocat")

        viewModel.uiState.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.sameUser).isTrue()
            assertThat(state.left).isNull()
            assertThat(state.isRefreshing).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(repo.refreshed).isEmpty()
    }

    @Test
    fun `refresh failure without cache keeps the error`() = runTest {
        val viewModel = viewModel(
            left = null,
            right = null,
            refreshResult = AppResult.Error(AppError.Network),
        )

        viewModel.uiState.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.leftError).isEqualTo(AppError.Network)
            assertThat(state.rightError).isEqualTo(AppError.Network)
            assertThat(state.isRefreshing).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh failure with cache emits a message and keeps the profile`() = runTest {
        val viewModel = viewModel(
            left = detail("octocat", followers = 1),
            right = detail("torvalds", followers = 2),
            refreshResult = AppResult.Error(AppError.Network),
        )

        viewModel.uiState.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.left).isNotNull()
            assertThat(state.leftError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.effects.test {
            viewModel.onEvent(CompareUiEvent.Refresh)
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(CompareUiEffect.ShowMessage("Could not refresh profile"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewModel(
        left: UserDetail? = null,
        right: UserDetail? = null,
        leftLogin: String = "octocat",
        rightLogin: String = "torvalds",
        refreshResult: AppResult<Unit> = AppResult.Success(Unit),
        repo: FakeCompareRepository = FakeCompareRepository(
            details = mapOf(leftLogin to left, rightLogin to right),
            refreshResult = refreshResult,
        ),
    ): CompareViewModel {
        return CompareViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "left" to leftLogin,
                    "right" to rightLogin,
                ),
            ),
            observeUserDetail = ObserveUserDetailUseCase(repo),
            refreshUserDetail = RefreshUserDetailUseCase(repo),
            compareProfiles = CompareProfilesUseCase(),
        )
    }

    private class FakeCompareRepository(
        private val details: Map<String, UserDetail?> = emptyMap(),
        private val refreshResult: AppResult<Unit> = AppResult.Success(Unit),
    ) : UserRepository {
        val refreshed = mutableListOf<String>()

        override fun searchUsers(query: String): Flow<PagingData<User>> = flowOf(PagingData.empty())

        override fun observeUserDetail(login: String): Flow<UserDetail?> =
            flowOf(details[login])

        override suspend fun refreshUserDetail(login: String): AppResult<Unit> {
            refreshed += login
            return refreshResult
        }

        override fun observeFavorites(): Flow<List<User>> = flowOf(emptyList())

        override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> =
            AppResult.Success(Unit)

        override fun observeIsFavorite(login: String): Flow<Boolean> = flowOf(false)
    }

    private fun detail(
        login: String,
        followers: Int = 0,
        languages: List<String> = emptyList(),
    ): UserDetail = UserDetail(
        id = 1,
        login = login,
        name = login,
        avatarUrl = "",
        htmlUrl = "https://github.com/$login",
        bio = null,
        company = null,
        location = null,
        blog = null,
        twitterUsername = null,
        publicRepos = 1,
        publicGists = 0,
        followers = followers,
        following = 0,
        createdAt = null,
        repositories = languages.mapIndexed { index, language ->
            Repository(
                id = index.toLong(),
                name = "repo$index",
                fullName = "$login/repo$index",
                description = null,
                htmlUrl = "https://github.com/$login/repo$index",
                language = language,
                stargazersCount = 0,
                forksCount = 0,
                updatedAt = null,
                isPrivate = false,
            )
        },
    )
}
