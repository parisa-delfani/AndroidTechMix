package com.androidtechmix.githubusers.feature.favorites.ui.state

import com.androidtechmix.githubusers.core.model.User

sealed interface FavoritesUiEvent {
    data class OpenUser(val login: String) : FavoritesUiEvent
    data class RemoveFavorite(val user: User) : FavoritesUiEvent
    data object ToggleCompareMode : FavoritesUiEvent
    data object CancelCompare : FavoritesUiEvent
    data class ToggleCompareSelection(val login: String) : FavoritesUiEvent
    data object ConfirmCompare : FavoritesUiEvent
}
