package com.androidtechmix.githubusers.core.network.error

import com.androidtechmix.githubusers.core.common.result.AppError
import com.google.common.truth.Truth.assertThat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorMapperTest {

    @Test
    fun `maps io exception to network error`() {
        assertThat(IOException("offline").mapToAppError()).isEqualTo(AppError.Network)
    }

    @Test
    fun `maps 404 to not found`() {
        assertThat(httpError(404).mapToAppError()).isEqualTo(AppError.NotFound)
    }

    @Test
    fun `maps 403 to rate limited`() {
        assertThat(httpError(403).mapToAppError()).isInstanceOf(AppError.RateLimited::class.java)
    }

    private fun httpError(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(code, body))
    }
}
