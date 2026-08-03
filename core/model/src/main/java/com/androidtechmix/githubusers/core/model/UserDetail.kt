package com.androidtechmix.githubusers.core.model

data class UserDetail(
    val id: Long,
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val htmlUrl: String,
    val bio: String?,
    val company: String?,
    val location: String?,
    val blog: String?,
    val twitterUsername: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String?,
    val isFavorite: Boolean = false,
    val repositories: List<Repository> = emptyList(),
)
