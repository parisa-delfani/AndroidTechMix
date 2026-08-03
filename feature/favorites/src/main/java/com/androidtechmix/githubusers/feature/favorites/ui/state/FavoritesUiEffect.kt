package com.androidtechmix.githubusers.feature.favorites.ui.state

sealed interface FavoritesUiEffect {
    data class NavigateToDetail(val login: String) : FavoritesUiEffect
    data class ShowMessage(val message: String) : FavoritesUiEffect
}
