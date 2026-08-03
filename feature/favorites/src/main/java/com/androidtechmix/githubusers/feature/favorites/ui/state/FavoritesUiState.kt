package com.androidtechmix.githubusers.feature.favorites.ui.state

import com.androidtechmix.githubusers.core.domain.model.User

data class FavoritesUiState(
    val favorites: List<User> = emptyList(),
    val isLoading: Boolean = true,
)
