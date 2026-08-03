package com.androidtechmix.githubusers.core.domain.usecase

import androidx.paging.PagingData
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.model.User
import com.androidtechmix.githubusers.core.domain.model.UserDetail
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchUsersUseCaseTest {

    @Test
    fun `trims query before searching`() = runTest {
        val repo = FakeUserRepository()
        val useCase = SearchUsersUseCase(repo)

        useCase("  octocat  ").first()

        assertThat(repo.lastSearchQuery).isEqualTo("octocat")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects blank query`() {
        val useCase = SearchUsersUseCase(FakeUserRepository())
        useCase("   ")
    }
}

class SetFavoriteUseCaseTest {

    @Test
    fun `delegates favorite toggle to repository`() = runTest {
        val repo = FakeUserRepository()
        val useCase = SetFavoriteUseCase(repo)

        val result = useCase("octocat", true)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        assertThat(repo.lastFavoriteLogin).isEqualTo("octocat")
        assertThat(repo.lastFavoriteValue).isTrue()
    }
}

private class FakeUserRepository : UserRepository {
    var lastSearchQuery: String? = null
    var lastFavoriteLogin: String? = null
    var lastFavoriteValue: Boolean? = null

    override fun searchUsers(query: String): Flow<PagingData<User>> {
        lastSearchQuery = query
        return flowOf(PagingData.empty())
    }

    override fun observeUserDetail(login: String): Flow<UserDetail?> = flowOf(null)

    override suspend fun refreshUserDetail(login: String): AppResult<Unit> = AppResult.Success(Unit)

    override fun observeFavorites(): Flow<List<User>> = flowOf(emptyList())

    override suspend fun setFavorite(login: String, favorite: Boolean): AppResult<Unit> {
        lastFavoriteLogin = login
        lastFavoriteValue = favorite
        return AppResult.Success(Unit)
    }

    override fun observeIsFavorite(login: String): Flow<Boolean> = flowOf(false)
}
