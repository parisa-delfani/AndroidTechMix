package com.androidtechmix.githubusers.feature.userdetail.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidtechmix.githubusers.core.model.Repository
import com.androidtechmix.githubusers.core.model.UserDetail
import com.androidtechmix.githubusers.core.designsystem.components.ErrorState
import com.androidtechmix.githubusers.core.designsystem.components.FullScreenLoading
import com.androidtechmix.githubusers.core.designsystem.components.UserAvatar
import com.androidtechmix.githubusers.feature.userdetail.ui.components.StatChip
import kotlinx.coroutines.flow.collectLatest
import com.androidtechmix.githubusers.feature.userdetail.R
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiEffect
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiEvent
import com.androidtechmix.githubusers.feature.userdetail.ui.state.UserDetailUiState

@Composable
fun UserDetailRoute(
    onBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                UserDetailUiEffect.NavigateBack -> onBack()
                is UserDetailUiEffect.ShareProfile -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.url)
                        putExtra(Intent.EXTRA_SUBJECT, effect.login)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
                is UserDetailUiEffect.OpenUrl -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(effect.url)))
                }
                is UserDetailUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    UserDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (UserDetailUiEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.login) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(UserDetailUiEvent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(UserDetailUiEvent.Share) }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.cd_share),
                        )
                    }
                    IconButton(onClick = { onEvent(UserDetailUiEvent.ToggleFavorite) }) {
                        val favorite = uiState.detail?.isFavorite == true
                        Icon(
                            imageVector = if (favorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = stringResource(
                                if (favorite) R.string.cd_unfavorite else R.string.cd_favorite,
                            ),
                            tint = if (favorite) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState.detail == null && uiState.isRefreshing -> {
                FullScreenLoading(modifier = Modifier.padding(padding))
            }
            uiState.detail == null && uiState.error != null -> {
                ErrorState(
                    error = uiState.error,
                    onRetry = { onEvent(UserDetailUiEvent.Refresh) },
                    modifier = Modifier.padding(padding),
                )
            }
            uiState.detail != null -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { onEvent(UserDetailUiEvent.Refresh) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    UserDetailContent(
                        detail = uiState.detail,
                        onOpenUrl = { onEvent(UserDetailUiEvent.OpenUrl(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    detail: UserDetail,
    onOpenUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                url = detail.avatarUrl,
                contentDescription = detail.login,
                size = 88,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = detail.name ?: detail.login,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "@${detail.login}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (!detail.bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = detail.bio.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatChip(
                label = stringResource(R.string.stat_repos),
                value = detail.publicRepos.toString(),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = stringResource(R.string.stat_followers),
                value = detail.followers.toString(),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = stringResource(R.string.stat_following),
                value = detail.following.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        val meta = listOfNotNull(
            detail.company?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.meta_company, it) },
            detail.location?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.meta_location, it) },
            detail.blog?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.meta_blog, it) },
        )
        if (meta.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            meta.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.repos_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (detail.repositories.isEmpty()) {
            Text(
                text = stringResource(R.string.repos_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            detail.repositories.forEach { repo ->
                RepoCard(
                    repository = repo,
                    onClick = { onOpenUrl(repo.htmlUrl) },
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun RepoCard(
    repository: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = repository.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!repository.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = repository.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.repo_stars, repository.stargazersCount),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = stringResource(R.string.repo_forks, repository.forksCount),
                    style = MaterialTheme.typography.labelLarge,
                )
                if (!repository.language.isNullOrBlank()) {
                    Text(
                        text = repository.language.orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
