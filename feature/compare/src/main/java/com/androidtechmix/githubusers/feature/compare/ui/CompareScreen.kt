package com.androidtechmix.githubusers.feature.compare.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.designsystem.components.EmptyState
import com.androidtechmix.githubusers.core.designsystem.components.ErrorState
import com.androidtechmix.githubusers.core.designsystem.components.FullScreenLoading
import com.androidtechmix.githubusers.core.designsystem.components.UserAvatar
import com.androidtechmix.githubusers.core.model.LanguageCount
import com.androidtechmix.githubusers.core.model.ProfileComparison
import com.androidtechmix.githubusers.core.model.SharedLanguage
import com.androidtechmix.githubusers.core.model.UserDetail
import com.androidtechmix.githubusers.feature.compare.R
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEffect
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiEvent
import com.androidtechmix.githubusers.feature.compare.ui.state.CompareUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CompareRoute(
    onBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                CompareUiEffect.NavigateBack -> onBack()
                is CompareUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CompareScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    uiState: CompareUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CompareUiEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.compare_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CompareUiEvent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState.invalidRoute -> {
                EmptyState(
                    title = stringResource(R.string.compare_invalid_title),
                    message = stringResource(R.string.compare_invalid_message),
                    modifier = Modifier.padding(padding),
                )
            }
            uiState.sameUser -> {
                EmptyState(
                    title = stringResource(R.string.compare_same_user_title),
                    message = stringResource(R.string.compare_same_user_message),
                    modifier = Modifier.padding(padding),
                )
            }
            uiState.left == null && uiState.right == null && uiState.isRefreshing -> {
                FullScreenLoading(modifier = Modifier.padding(padding))
            }
            uiState.left == null && uiState.right == null -> {
                ErrorState(
                    error = uiState.leftError ?: uiState.rightError ?: AppError.Unknown(),
                    onRetry = { onEvent(CompareUiEvent.Refresh) },
                    modifier = Modifier.padding(padding),
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { onEvent(CompareUiEvent.Refresh) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    CompareContent(
                        uiState = uiState,
                        onRetry = { onEvent(CompareUiEvent.Refresh) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareContent(
    uiState: CompareUiState,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ProfileColumn(
                detail = uiState.left,
                login = uiState.leftLogin,
                error = uiState.leftError,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            ProfileColumn(
                detail = uiState.right,
                login = uiState.rightLogin,
                error = uiState.rightError,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
        }

        val left = uiState.left
        val right = uiState.right
        val comparison = uiState.comparison
        if (left != null && right != null && comparison != null) {
            Spacer(modifier = Modifier.height(24.dp))
            MetricRow(
                label = stringResource(R.string.compare_repos),
                left = left.publicRepos,
                right = right.publicRepos,
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetricRow(
                label = stringResource(R.string.compare_followers),
                left = left.followers,
                right = right.followers,
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetricRow(
                label = stringResource(R.string.compare_following),
                left = left.following,
                right = right.following,
            )
            Spacer(modifier = Modifier.height(24.dp))
            LanguagesSection(
                leftLogin = left.login,
                rightLogin = right.login,
                comparison = comparison,
            )
        }
    }
}

@Composable
private fun ProfileColumn(
    detail: UserDetail?,
    login: String,
    error: AppError?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (detail != null) {
            UserAvatar(
                url = detail.avatarUrl,
                contentDescription = detail.login,
                size = 72,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = detail.name ?: detail.login,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "@${detail.login}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Text(
                text = login,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.compare_side_failed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(R.string.compare_retry))
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    left: Int,
    right: Int,
) {
    val max = maxOf(left, right, 1).toFloat()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = left.toString(),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { (left.toFloat() / max).coerceIn(0f, 1f) },
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { (right.toFloat() / max).coerceIn(0f, 1f) },
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = right.toString(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun LanguagesSection(
    leftLogin: String,
    rightLogin: String,
    comparison: ProfileComparison,
) {
    Text(
        text = stringResource(R.string.compare_languages_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(modifier = Modifier.height(12.dp))
    val empty = comparison.shared.isEmpty() &&
        comparison.leftOnly.isEmpty() &&
        comparison.rightOnly.isEmpty()
    if (empty) {
        Text(
            text = stringResource(R.string.compare_languages_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    Text(
        text = stringResource(R.string.compare_shared),
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(modifier = Modifier.height(8.dp))
    if (comparison.shared.isEmpty()) {
        Text(
            text = stringResource(R.string.compare_shared_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        comparison.shared.forEach { language ->
            SharedLanguageRow(language)
        }
    }

    if (comparison.leftOnly.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        LanguageList(
            title = stringResource(R.string.compare_only_on, leftLogin),
            languages = comparison.leftOnly,
        )
    }
    if (comparison.rightOnly.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        LanguageList(
            title = stringResource(R.string.compare_only_on, rightLogin),
            languages = comparison.rightOnly,
        )
    }
}

@Composable
private fun SharedLanguageRow(language: SharedLanguage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = language.language,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(
                R.string.compare_shared_counts,
                language.leftCount,
                language.rightCount,
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LanguageList(
    title: String,
    languages: List<LanguageCount>,
) {
    Text(text = title, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))
    languages.forEach { language ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = language.language,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = language.repoCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
