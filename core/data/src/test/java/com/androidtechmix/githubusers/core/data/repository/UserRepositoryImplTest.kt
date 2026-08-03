package com.androidtechmix.githubusers.core.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.database.GitHubDatabase
import com.androidtechmix.githubusers.core.network.api.GitHubApi
import com.androidtechmix.githubusers.core.network.dto.RepositoryDto
import com.androidtechmix.githubusers.core.network.dto.SearchUsersResponseDto
import com.androidtechmix.githubusers.core.network.dto.UserDetailDto
import com.androidtechmix.githubusers.core.network.dto.UserDto
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserRepositoryImplTest {

    private lateinit var database: GitHubDatabase
    private lateinit var repository: UserRepositoryImpl

    private val fakeApi = object : GitHubApi {
        override suspend fun searchUsers(query: String, page: Int, perPage: Int) =
            SearchUsersResponseDto(totalCount = 1, items = listOf(sampleUserDto))

        override suspend fun getUser(username: String) = sampleDetailDto

        override suspend fun getUserRepos(
            username: String,
            sort: String,
            direction: String,
            perPage: Int,
            page: Int,
        ) = listOf(sampleRepoDto)
    }

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GitHubDatabase::class.java,
        ).allowMainThreadQueries().build()

        repository = UserRepositoryImpl(
            api = fakeApi,
            database = database,
            userDao = database.userDao(),
            userDetailDao = database.userDetailDao(),
            favoriteDao = database.favoriteDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `refreshUserDetail writes profile and repos into room`() = runTest {
        val result = repository.refreshUserDetail("octocat")
        assertThat(result).isInstanceOf(AppResult.Success::class.java)

        val detail = repository.observeUserDetail("octocat").first()
        assertThat(detail).isNotNull()
        assertThat(detail!!.login).isEqualTo("octocat")
        assertThat(detail.repositories).hasSize(1)
        assertThat(detail.repositories.first().name).isEqualTo("Hello-World")
    }

    @Test
    fun `setFavorite persists and observeFavorites emits user`() = runTest {
        repository.refreshUserDetail("octocat")
        repository.setFavorite("octocat", true)

        val favorites = repository.observeFavorites().first()
        assertThat(favorites).hasSize(1)
        assertThat(favorites.first().login).isEqualTo("octocat")
        assertThat(favorites.first().isFavorite).isTrue()
    }

    private companion object {
        val sampleUserDto = UserDto(
            id = 1,
            login = "octocat",
            avatarUrl = "https://example.com/a.png",
            htmlUrl = "https://github.com/octocat",
            type = "User",
            score = 1.0,
        )
        val sampleDetailDto = UserDetailDto(
            id = 1,
            login = "octocat",
            name = "The Octocat",
            avatarUrl = "https://example.com/a.png",
            htmlUrl = "https://github.com/octocat",
            bio = "GitHub mascot",
            publicRepos = 8,
            followers = 100,
            following = 10,
        )
        val sampleRepoDto = RepositoryDto(
            id = 10,
            name = "Hello-World",
            fullName = "octocat/Hello-World",
            description = "demo",
            htmlUrl = "https://github.com/octocat/Hello-World",
            language = "Kotlin",
            stargazersCount = 42,
            forksCount = 3,
        )
    }
}
