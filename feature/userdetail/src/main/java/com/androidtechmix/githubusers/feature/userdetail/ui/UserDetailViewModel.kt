package com.androidtechmix.githubusers.feature.userdetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.usecase.ObserveUserDetailUseCase
import com.androidtechmix.githubusers.core.domain.usecase.RefreshUserDetailUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SetFavoriteUseCase
import com.androidtechmix.githubusers.feature.userdetail.navigation.UserDetailDestination
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiEffect
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiEvent
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiState
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
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeUserDetail: ObserveUserDetailUseCase,
    private val refreshUserDetail: RefreshUserDetailUseCase,
    private val setFavorite: SetFavoriteUseCase,
) : ViewModel() {

    private val login: String = savedStateHandle.toRoute<UserDetailDestination>().login

    private val refreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<AppError?>(null)

    val uiState: StateFlow<UserDetailUiState> = combine(
        observeUserDetail(login),
        refreshing,
        error,
    ) { detail, isRefreshing, appError ->
        UserDetailUiState(
            login = login,
            detail = detail,
            isRefreshing = isRefreshing,
            error = if (detail == null) appError else null,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserDetailUiState(login = login, isRefreshing = true),
    )

    private val _effects = Channel<UserDetailUiEffect>(Channel.BUFFERED)
    val effects: Flow<UserDetailUiEffect> = _effects.receiveAsFlow()

    init {
        refresh()
    }

    fun onEvent(event: UserDetailUiEvent) {
        when (event) {
            UserDetailUiEvent.Refresh -> refresh()
            UserDetailUiEvent.ToggleFavorite -> toggleFavorite()
            UserDetailUiEvent.Share -> {
                val url = uiState.value.detail?.htmlUrl ?: return
                viewModelScope.launch {
                    _effects.send(UserDetailUiEffect.ShareProfile(url, login))
                }
            }
            UserDetailUiEvent.NavigateBack -> {
                viewModelScope.launch { _effects.send(UserDetailUiEffect.NavigateBack) }
            }
            is UserDetailUiEvent.OpenUrl -> {
                viewModelScope.launch { _effects.send(UserDetailUiEffect.OpenUrl(event.url)) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            when (val result = refreshUserDetail(login)) {
                is AppResult.Success -> error.value = null
                is AppResult.Error -> {
                    if (uiState.value.detail == null) {
                        error.value = result.error
                    } else {
                        _effects.send(UserDetailUiEffect.ShowMessage("Could not refresh profile"))
                    }
                }
            }
            refreshing.value = false
        }
    }

    private fun toggleFavorite() {
        val detail = uiState.value.detail ?: return
        viewModelScope.launch {
            when (setFavorite(detail.login, !detail.isFavorite)) {
                is AppResult.Success -> Unit
                is AppResult.Error -> {
                    _effects.send(UserDetailUiEffect.ShowMessage("Could not update favorite"))
                }
            }
        }
    }
}
