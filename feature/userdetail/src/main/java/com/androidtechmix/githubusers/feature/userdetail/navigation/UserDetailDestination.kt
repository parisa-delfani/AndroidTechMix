package com.androidtechmix.githubusers.feature.userdetail.navigation

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailDestination(
    val login: String,
)
