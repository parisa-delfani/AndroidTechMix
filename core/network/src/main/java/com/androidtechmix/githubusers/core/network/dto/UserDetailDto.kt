package com.androidtechmix.githubusers.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailDto(
    val id: Long,
    val login: String,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    val bio: String? = null,
    val company: String? = null,
    val location: String? = null,
    val blog: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("public_repos") val publicRepos: Int = 0,
    @SerialName("public_gists") val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
)
