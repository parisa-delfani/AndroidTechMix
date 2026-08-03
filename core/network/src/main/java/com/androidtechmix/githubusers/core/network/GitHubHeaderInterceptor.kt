package com.androidtechmix.githubusers.core.network

import com.androidtechmix.githubusers.core.common.util.Constants
import okhttp3.Interceptor
import okhttp3.Response

class GitHubHeaderInterceptor(
    private val tokenProvider: () -> String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", Constants.GITHUB_API_VERSION)
            .header("User-Agent", "AndroidTechMix")

        val token = tokenProvider().trim()
        if (token.isNotEmpty()) {
            builder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
