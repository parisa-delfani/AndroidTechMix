package com.androidtechmix.githubusers.feature.compare.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.domain.usecase.CompareProfilesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.ObserveUserDetailUseCase
import com.androidtechmix.githubusers.core.domain.usecase.RefreshUserDetailUseCase
import com.androidtechmix.githubusers.feature.compare.navigation.CompareDestination
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEffect
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEvent
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeUserDetail: ObserveUserDetailUseCase,
    private val refreshUserDetail: RefreshUserDetailUseCase,
    private val compareProfiles: CompareProfilesUseCase,
) : ViewModel() {

    private val leftLogin: String
    private val rightLogin: String
    private val sameUser: Boolean
    private val invalidRoute: Boolean

    private val refreshing = MutableStateFlow(false)
    private val leftError = MutableStateFlow<AppError?>(null)
    private val rightError = MutableStateFlow<AppError?>(null)
    private var refreshJob: Job? = null

    val uiState: StateFlow<CompareUiState>

    private val _effects = Channel<CompareUiEffect>(Channel.BUFFERED)
    val effects: Flow<CompareUiEffect> = _effects.receiveAsFlow()

    init {
        val route = savedStateHandle.toRoute<CompareDestination>()
        leftLogin = route.left.trim()
        rightLogin = route.right.trim()
        sameUser = leftLogin.equals(rightLogin, ignoreCase = true)
        invalidRoute = leftLogin.isBlank() || rightLogin.isBlank()

        val canCompare = !sameUser && !invalidRoute
        if (canCompare) refreshing.value = true
        val leftFlow = if (canCompare) {
            observeUserDetail(leftLogin).distinctUntilChanged()
        } else {
            flowOf(null)
        }
        val rightFlow = if (canCompare) {
            observeUserDetail(rightLogin).distinctUntilChanged()
        } else {
            flowOf(null)
        }

        uiState = combine(
            leftFlow,
            rightFlow,
            refreshing,
            leftError,
            rightError,
        ) { left, right, isRefreshing, leftFailure, rightFailure ->
            CompareUiState(
                leftLogin = leftLogin,
                rightLogin = rightLogin,
                sameUser = sameUser,
                invalidRoute = invalidRoute,
                left = left,
                right = right,
                comparison = if (left != null && right != null) compareProfiles(left, right) else null,
                isRefreshing = isRefreshing,
                leftError = if (left == null) leftFailure else null,
                rightError = if (right == null) rightFailure else null,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CompareUiState(
                leftLogin = leftLogin,
                rightLogin = rightLogin,
                sameUser = sameUser,
                invalidRoute = invalidRoute,
                isRefreshing = canCompare,
            ),
        )

        if (canCompare) refresh()
    }

    fun onEvent(event: CompareUiEvent) {
        when (event) {
            CompareUiEvent.Refresh -> refresh()
            CompareUiEvent.NavigateBack -> {
                viewModelScope.launch { _effects.send(CompareUiEffect.NavigateBack) }
            }
        }
    }

    private fun refresh() {
        if (sameUser || invalidRoute) return
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            refreshing.value = true
            try {
                val (leftResult, rightResult) = coroutineScope {
                    val left = async { refreshUserDetail(leftLogin) }
                    val right = async { refreshUserDetail(rightLogin) }
                    left.await() to right.await()
                }
                val notifyLeft = applyResult(leftResult, uiState.value.left != null, leftError)
                val notifyRight = applyResult(rightResult, uiState.value.right != null, rightError)
                if (notifyLeft || notifyRight) {
                    _effects.send(CompareUiEffect.ShowMessage("Could not refresh profile"))
                }
            } finally {
                refreshing.value = false
            }
        }
    }

    private fun applyResult(
        result: AppResult<Unit>,
        hadData: Boolean,
        error: MutableStateFlow<AppError?>,
    ): Boolean {
        return when (result) {
            is AppResult.Success -> {
                error.value = null
                false
            }
            is AppResult.Error -> {
                if (hadData) {
                    true
                } else {
                    error.value = result.error
                    false
                }
            }
        }
    }
}
