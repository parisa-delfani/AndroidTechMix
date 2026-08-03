package com.androidtechmix.githubusers.core.model

data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val updatedAt: String?,
    val isPrivate: Boolean,
)
