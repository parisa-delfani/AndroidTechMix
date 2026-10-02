package com.androidtechmix.githubusers.feature.favorites.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidtechmix.githubusers.core.designsystem.components.EmptyState
import com.androidtechmix.githubusers.core.designsystem.components.FullScreenLoading
import com.androidtechmix.githubusers.core.designsystem.components.UserListItem
import com.androidtechmix.githubusers.feature.favorites.R
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEffect
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEvent
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FavoritesRoute(
    onOpenUser: (String) -> Unit,
    onGoToSearch: () -> Unit,
    onCompare: (left: String, right: String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is FavoritesUiEffect.NavigateToDetail -> onOpenUser(effect.login)
                is FavoritesUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is FavoritesUiEffect.NavigateToCompare -> onCompare(effect.left, effect.right)
            }
        }
    }

    FavoritesScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onGoToSearch = onGoToSearch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (FavoritesUiEvent) -> Unit,
    onGoToSearch: () -> Unit,
) {
    BackHandler(enabled = uiState.compareMode) {
        onEvent(FavoritesUiEvent.CancelCompare)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (uiState.compareMode) {
                                R.string.compare_select_title
                            } else {
                                R.string.favorites_title
                            },
                        ),
                    )
                },
                navigationIcon = {
                    if (uiState.compareMode) {
                        IconButton(onClick = { onEvent(FavoritesUiEvent.CancelCompare) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.cd_close_compare),
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.compareMode) {
                        TextButton(
                            onClick = { onEvent(FavoritesUiEvent.ConfirmCompare) },
                            enabled = uiState.selectedLogins.size == 2,
                        ) {
                            Text(text = stringResource(R.string.compare_action))
                        }
                    } else if (uiState.favorites.size >= 2) {
                        IconButton(onClick = { onEvent(FavoritesUiEvent.ToggleCompareMode) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                contentDescription = stringResource(R.string.cd_compare),
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState.isLoading -> FullScreenLoading(modifier = Modifier.padding(padding))
            uiState.favorites.isEmpty() -> {
                EmptyState(
                    title = stringResource(R.string.favorites_empty_title),
                    message = stringResource(R.string.favorites_empty_message),
                    actionLabel = stringResource(R.string.favorites_go_search),
                    onAction = onGoToSearch,
                    modifier = Modifier.padding(padding),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        items = uiState.favorites,
                        key = { it.id },
                    ) { user ->
                        val selected = user.login in uiState.selectedLogins
                        UserListItem(
                            login = user.login,
                            avatarUrl = user.avatarUrl,
                            type = user.type,
                            isFavorite = user.isFavorite,
                            onClick = {
                                if (uiState.compareMode) {
                                    onEvent(FavoritesUiEvent.ToggleCompareSelection(user.login))
                                } else {
                                    onEvent(FavoritesUiEvent.OpenUser(user.login))
                                }
                            },
                            onFavoriteClick = if (uiState.compareMode) {
                                null
                            } else {
                                { onEvent(FavoritesUiEvent.RemoveFavorite(user)) }
                            },
                            modifier = if (selected) {
                                Modifier
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.medium,
                                    )
                                    .semantics { this.selected = true }
                            } else {
                                Modifier
                            },
                        )
                    }
                }
            }
        }
    }
}
