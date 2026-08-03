package com.androidtechmix.githubusers.feature.search.ui.state

import com.androidtechmix.githubusers.core.model.User

sealed interface SearchUiEvent {
    data class QueryChanged(val query: String) : SearchUiEvent
    data class ToggleFavorite(val user: User) : SearchUiEvent
    data class OpenUser(val login: String) : SearchUiEvent
}
