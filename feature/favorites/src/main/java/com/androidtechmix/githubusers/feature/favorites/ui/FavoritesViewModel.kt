package com.androidtechmix.githubusers.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.usecase.ObserveFavoritesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SetFavoriteUseCase
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEffect
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEvent
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val setFavorite: SetFavoriteUseCase,
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = observeFavorites()
        .map { FavoritesUiState(favorites = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState(),
        )

    private val _effects = Channel<FavoritesUiEffect>(Channel.BUFFERED)
    val effects: Flow<FavoritesUiEffect> = _effects.receiveAsFlow()

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.OpenUser -> {
                viewModelScope.launch {
                    _effects.send(FavoritesUiEffect.NavigateToDetail(event.login))
                }
            }
            is FavoritesUiEvent.RemoveFavorite -> {
                viewModelScope.launch {
                    when (setFavorite(event.user.login, false)) {
                        is AppResult.Success -> Unit
                        is AppResult.Error -> {
                            _effects.send(FavoritesUiEffect.ShowMessage("Could not remove favorite"))
                        }
                    }
                }
            }
        }
    }
}
