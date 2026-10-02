package com.androidtechmix.githubusers.feature.compare.ui.state

import com.androidtechmix.githubusers.core.common.result.AppError
import com.androidtechmix.githubusers.core.model.ProfileComparison
import com.androidtechmix.githubusers.core.model.UserDetail

data class CompareUiState(
    val leftLogin: String = "",
    val rightLogin: String = "",
    val sameUser: Boolean = false,
    val invalidRoute: Boolean = false,
    val left: UserDetail? = null,
    val right: UserDetail? = null,
    val comparison: ProfileComparison? = null,
    val isRefreshing: Boolean = false,
    val leftError: AppError? = null,
    val rightError: AppError? = null,
)
