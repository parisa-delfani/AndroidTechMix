package com.androidtechmix.githubusers.core.model

data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
    val score: Double? = null,
    val isFavorite: Boolean = false,
)
