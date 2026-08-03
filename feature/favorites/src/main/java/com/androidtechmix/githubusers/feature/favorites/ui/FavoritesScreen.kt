package com.androidtechmix.githubusers.feature.favorites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidtechmix.githubusers.core.ui.components.EmptyState
import com.androidtechmix.githubusers.core.ui.components.FullScreenLoading
import com.androidtechmix.githubusers.core.ui.components.UserListItem
import com.androidtechmix.githubusers.feature.favorites.R
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEffect
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiEvent
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FavoritesRoute(
    onOpenUser: (String) -> Unit,
    onGoToSearch: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is FavoritesUiEffect.NavigateToDetail -> onOpenUser(effect.login)
                is FavoritesUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
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
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.favorites_title)) })
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
                        UserListItem(
                            user = user,
                            onClick = { onEvent(FavoritesUiEvent.OpenUser(user.login)) },
                            onFavoriteClick = { onEvent(FavoritesUiEvent.RemoveFavorite(user)) },
                        )
                    }
                }
            }
        }
    }
}
