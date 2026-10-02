package com.androidtechmix.githubusers.core.model

data class LanguageCount(
    val language: String,
    val repoCount: Int,
)

data class SharedLanguage(
    val language: String,
    val leftCount: Int,
    val rightCount: Int,
)

data class ProfileComparison(
    val shared: List<SharedLanguage> = emptyList(),
    val leftOnly: List<LanguageCount> = emptyList(),
    val rightOnly: List<LanguageCount> = emptyList(),
)
