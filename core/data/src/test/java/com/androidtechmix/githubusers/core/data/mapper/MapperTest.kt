package com.androidtechmix.githubusers.core.data.mapper

import com.androidtechmix.githubusers.core.network.dto.UserDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MapperTest {

    @Test
    fun `maps user dto to entity`() {
        val dto = UserDto(
            id = 1,
            login = "octocat",
            avatarUrl = "https://example.com/a.png",
            htmlUrl = "https://github.com/octocat",
            type = "User",
            score = 42.0,
        )

        val entity = dto.toEntity()

        assertThat(entity.id).isEqualTo(1)
        assertThat(entity.login).isEqualTo("octocat")
        assertThat(entity.avatarUrl).isEqualTo("https://example.com/a.png")
        assertThat(entity.score).isEqualTo(42.0)
    }

    @Test
    fun `maps entity to domain with favorite flag`() {
        val entity = UserDto(
            id = 2,
            login = "hubot",
            avatarUrl = "https://example.com/b.png",
            htmlUrl = "https://github.com/hubot",
            type = "User",
        ).toEntity()

        val domain = entity.toDomain(isFavorite = true)

        assertThat(domain.isFavorite).isTrue()
        assertThat(domain.login).isEqualTo("hubot")
    }
}
