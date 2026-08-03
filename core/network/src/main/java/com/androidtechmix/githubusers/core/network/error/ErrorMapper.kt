package com.androidtechmix.githubusers.core.network.error

import com.androidtechmix.githubusers.core.common.result.AppError
import retrofit2.HttpException
import java.io.IOException

fun Throwable.mapToAppError(): AppError = when (this) {
    is IOException -> AppError.Network
    is HttpException -> when (code()) {
        404 -> AppError.NotFound
        403, 429 -> {
            val retryAfter = response()?.headers()?.get("retry-after")?.toLongOrNull()
            AppError.RateLimited(retryAfter)
        }
        else -> AppError.Http(code(), message())
    }
    else -> AppError.Unknown(message)
}
