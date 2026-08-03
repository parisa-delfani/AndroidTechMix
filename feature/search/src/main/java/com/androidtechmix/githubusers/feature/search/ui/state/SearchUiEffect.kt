package com.androidtechmix.githubusers.feature.search.ui.state

sealed interface SearchUiEffect {
    data class NavigateToDetail(val login: String) : SearchUiEffect
    data class ShowMessage(val message: String) : SearchUiEffect
}
