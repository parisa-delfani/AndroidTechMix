package com.androidtechmix.githubusers.feature.compare.ui.state

sealed interface CompareUiEffect {
    data object NavigateBack : CompareUiEffect
    data class ShowMessage(val message: String) : CompareUiEffect
}
