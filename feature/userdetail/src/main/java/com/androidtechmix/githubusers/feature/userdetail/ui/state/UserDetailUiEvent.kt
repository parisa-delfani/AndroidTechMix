package com.androidtechmix.githubusers.feature.userdetail.ui.state

sealed interface UserDetailUiEvent {
    data object Refresh : UserDetailUiEvent
    data object ToggleFavorite : UserDetailUiEvent
    data object Share : UserDetailUiEvent
    data object NavigateBack : UserDetailUiEvent
    data class OpenUrl(val url: String) : UserDetailUiEvent
}
