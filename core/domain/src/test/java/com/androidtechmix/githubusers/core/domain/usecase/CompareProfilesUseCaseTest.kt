package com.androidtechmix.githubusers.core.domain.usecase

import com.androidtechmix.githubusers.core.model.Repository
import com.androidtechmix.githubusers.core.model.UserDetail
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CompareProfilesUseCaseTest {

    private val useCase = CompareProfilesUseCase()

    @Test
    fun `splits shared and unique languages`() {
        val left = detail("octocat", languages = listOf("Kotlin", "Kotlin", "Java", null, "  "))
        val right = detail("torvalds", languages = listOf("kotlin", "Go"))

        val comparison = useCase(left, right)

        assertThat(comparison.shared).hasSize(1)
        assertThat(comparison.shared.single().language).isEqualTo("Kotlin")
        assertThat(comparison.shared.single().leftCount).isEqualTo(2)
        assertThat(comparison.shared.single().rightCount).isEqualTo(1)
        assertThat(comparison.leftOnly.map { it.language }).containsExactly("Java")
        assertThat(comparison.rightOnly.map { it.language }).containsExactly("Go")
    }

    @Test
    fun `orders languages by count then name`() {
        val left = detail("a", languages = listOf("Go", "Kotlin", "Kotlin", "Java", "Java"))
        val right = detail("b", languages = listOf("Rust"))

        val comparison = useCase(left, right)

        assertThat(comparison.leftOnly.map { it.language }).containsExactly("Java", "Kotlin", "Go").inOrder()
    }

    @Test
    fun `empty repositories produce an empty comparison`() {
        val comparison = useCase(detail("a"), detail("b"))

        assertThat(comparison.shared).isEmpty()
        assertThat(comparison.leftOnly).isEmpty()
        assertThat(comparison.rightOnly).isEmpty()
    }

    private fun detail(
        login: String,
        languages: List<String?> = emptyList(),
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
        publicRepos = languages.size,
        publicGists = 0,
        followers = 0,
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
