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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val setFavorite: SetFavoriteUseCase,
) : ViewModel() {

    private val compareMode = MutableStateFlow(false)
    private val selectedLogins = MutableStateFlow<List<String>>(emptyList())

    val uiState: StateFlow<FavoritesUiState> = combine(
        observeFavorites(),
        compareMode,
        selectedLogins,
    ) { favorites, mode, selected ->
        val visibleLogins = favorites.map { it.login }.toSet()
        val inMode = mode && favorites.size >= 2
        FavoritesUiState(
            favorites = favorites,
            isLoading = false,
            compareMode = inMode,
            selectedLogins = if (inMode) selected.filter { it in visibleLogins }.take(2) else emptyList(),
        )
    }.stateIn(
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
            FavoritesUiEvent.ToggleCompareMode -> {
                if (uiState.value.favorites.size >= 2) {
                    compareMode.value = true
                }
            }
            FavoritesUiEvent.CancelCompare -> clearCompare()
            is FavoritesUiEvent.ToggleCompareSelection -> toggleSelection(event.login)
            FavoritesUiEvent.ConfirmCompare -> confirmCompare()
        }
    }

    private fun toggleSelection(login: String) {
        if (!compareMode.value) return
        val visible = uiState.value.favorites.map { it.login }.toSet()
        if (login !in visible) return
        val current = selectedLogins.value.filter { it in visible }
        selectedLogins.value = when {
            login in current -> current.filterNot { it == login }
            current.size >= 2 -> {
                viewModelScope.launch {
                    _effects.send(FavoritesUiEffect.ShowMessage("You can compare two users"))
                }
                current
            }
            else -> current + login
        }
    }

    private fun confirmCompare() {
        val visible = uiState.value.favorites.map { it.login }.toSet()
        val selected = selectedLogins.value.filter { it in visible }.take(2)
        if (selected.size != 2) return
        viewModelScope.launch {
            _effects.send(FavoritesUiEffect.NavigateToCompare(selected[0], selected[1]))
        }
        clearCompare()
    }

    private fun clearCompare() {
        compareMode.value = false
        selectedLogins.value = emptyList()
    }
}
