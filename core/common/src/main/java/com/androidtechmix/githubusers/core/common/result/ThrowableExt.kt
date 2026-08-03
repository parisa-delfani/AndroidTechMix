package com.androidtechmix.githubusers.core.common.result

import java.io.IOException

/**
 * Domain-friendly exception used to carry [AppError] across layer boundaries
 * (e.g. Paging RemoteMediator → UI) without leaking networking types.
 */
class AppException(
    val error: AppError,
    cause: Throwable? = null,
) : Exception(error.toString(), cause)

fun Throwable.toAppError(): AppError = when (this) {
    is AppException -> error
    is IOException -> AppError.Network
    else -> cause?.let { nested ->
        if (nested !== this) nested.toAppError() else null
    } ?: AppError.Unknown(message)
}
