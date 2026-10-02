package com.androidtechmix.githubusers.feature.compare.ui.state

sealed interface CompareUiEvent {
    data object Refresh : CompareUiEvent
    data object NavigateBack : CompareUiEvent
}
