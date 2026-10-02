package com.androidtechmix.githubusers.feature.compare.navigation

import kotlinx.serialization.Serializable

@Serializable
data class CompareDestination(
    val left: String,
    val right: String,
)
