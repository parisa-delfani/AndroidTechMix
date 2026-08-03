package com.androidtechmix.githubusers.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.androidtechmix.githubusers.core.common.result.toAppError
import com.androidtechmix.githubusers.core.model.User
import com.androidtechmix.githubusers.core.designsystem.components.EmptyState
import com.androidtechmix.githubusers.core.designsystem.components.ErrorState
import com.androidtechmix.githubusers.core.designsystem.components.UserListItem
import com.androidtechmix.githubusers.feature.search.R
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEffect
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiEvent
import com.androidtechmix.githubusers.feature.search.ui.state.SearchUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchRoute(
    onOpenUser: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users = viewModel.users.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SearchUiEffect.NavigateToDetail -> onOpenUser(effect.login)
                is SearchUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SearchScreen(
        uiState = uiState,
        users = users,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    users: LazyPagingItems<User>,
    snackbarHostState: SnackbarHostState,
    onEvent: (SearchUiEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val refreshState = users.loadState.refresh
    val isRefreshing = refreshState is LoadState.Loading && users.itemCount > 0

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search_title)) },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { onEvent(SearchUiEvent.QueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(text = stringResource(R.string.search_hint)) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { onEvent(SearchUiEvent.QueryChanged("")) }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.cd_clear_search),
                            )
                        }
                    }
                },
                singleLine = true,
            )

            when {
                uiState.submittedQuery.length < 2 -> {
                    EmptyState(
                        title = stringResource(R.string.search_empty_title),
                        message = stringResource(R.string.search_empty_message),
                    )
                }
                refreshState is LoadState.Loading && users.itemCount == 0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                refreshState is LoadState.Error && users.itemCount == 0 -> {
                    ErrorState(
                        error = refreshState.error.toAppError(),
                        onRetry = { users.retry() },
                    )
                }
                users.itemCount == 0 && refreshState is LoadState.NotLoading -> {
                    EmptyState(
                        title = stringResource(R.string.search_no_results_title),
                        message = stringResource(R.string.search_no_results_message, uiState.submittedQuery),
                    )
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { users.refresh() },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(
                                count = users.itemCount,
                                key = users.itemKey { it.id },
                            ) { index ->
                                val user = users[index] ?: return@items
                                UserListItem(
                                    login = user.login,
                                    avatarUrl = user.avatarUrl,
                                    type = user.type,
                                    isFavorite = user.isFavorite,
                                    onClick = { onEvent(SearchUiEvent.OpenUser(user.login)) },
                                    onFavoriteClick = { onEvent(SearchUiEvent.ToggleFavorite(user)) },
                                )
                            }

                            if (users.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
