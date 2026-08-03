package com.androidtechmix.githubusers.feature.userdetail.ui.state

sealed interface UserDetailUiEffect {
    data object NavigateBack : UserDetailUiEffect
    data class ShareProfile(val url: String, val login: String) : UserDetailUiEffect
    data class OpenUrl(val url: String) : UserDetailUiEffect
    data class ShowMessage(val message: String) : UserDetailUiEffect
}
