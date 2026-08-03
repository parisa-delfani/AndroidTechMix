package com.androidtechmix.githubusers.feature.userdetail.ui.state

import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.domain.model.UserDetail

data class UserDetailUiState(
    val login: String = "",
    val detail: UserDetail? = null,
    val isRefreshing: Boolean = false,
    val error: AppError? = null,
)
