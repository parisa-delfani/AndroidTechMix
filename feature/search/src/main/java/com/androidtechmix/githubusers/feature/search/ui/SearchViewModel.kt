package com.androidtechmix.githubusers.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.androidtechmix.githubusers.core.common.result.AppResult
import com.androidtechmix.githubusers.core.common.util.Constants
import com.androidtechmix.githubusers.core.domain.model.User
import com.androidtechmix.githubusers.core.domain.usecase.ObserveFavoritesUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SearchUsersUseCase
import com.androidtechmix.githubusers.core.domain.usecase.SetFavoriteUseCase
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEffect
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEvent
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUsers: SearchUsersUseCase,
    private val setFavorite: SetFavoriteUseCase,
    observeFavorites: ObserveFavoritesUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val submittedQuery = query
        .debounce(Constants.SEARCH_DEBOUNCE_MS)
        .map { it.trim() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "",
        )

    val uiState: StateFlow<SearchUiState> = combine(query, submittedQuery) { raw, submitted ->
        SearchUiState(query = raw, submittedQuery = submitted)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(),
    )

    private val _effects = Channel<SearchUiEffect>(Channel.BUFFERED)
    val effects: Flow<SearchUiEffect> = _effects.receiveAsFlow()

    private val favoriteLogins: StateFlow<Set<String>> = observeFavorites()
        .map { favorites -> favorites.map { it.login }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    /**
     * Cache the pager upstream first, then overlay favorite flags.
     * Combining with favorites *before* [cachedIn] causes
     * "Attempt to collect twice from pageEventFlow".
     */
    private val pagingData: Flow<PagingData<User>> = submittedQuery
        .flatMapLatest { submitted ->
            if (submitted.length < 2) {
                flowOf(PagingData.empty())
            } else {
                searchUsers(submitted)
            }
        }
        .cachedIn(viewModelScope)

    val users: Flow<PagingData<User>> = combine(pagingData, favoriteLogins) { data, favorites ->
        data.map { user -> user.copy(isFavorite = user.login in favorites) }
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.QueryChanged -> query.value = event.query
            is SearchUiEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    val result = setFavorite(event.user.login, !event.user.isFavorite)
                    if (result is AppResult.Error) {
                        _effects.send(SearchUiEffect.ShowMessage("Could not update favorite"))
                    }
                }
            }
            is SearchUiEvent.OpenUser -> {
                viewModelScope.launch {
                    _effects.send(SearchUiEffect.NavigateToDetail(event.login))
                }
            }
        }
    }
}
