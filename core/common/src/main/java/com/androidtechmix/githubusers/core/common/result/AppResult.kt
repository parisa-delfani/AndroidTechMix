package com.androidtechmix.githubusers.core.common.result

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}

sealed interface AppError {
    data object Network : AppError
    data object NotFound : AppError
    data class RateLimited(val retryAfterSeconds: Long? = null) : AppError
    data class Http(val code: Int, val message: String? = null) : AppError
    data class Unknown(val message: String? = null) : AppError
}

fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> data
    is AppResult.Error -> null
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(error)
    return this
}
