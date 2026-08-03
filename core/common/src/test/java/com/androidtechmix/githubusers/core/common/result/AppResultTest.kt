package com.androidtechmix.githubusers.core.common.result

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.IOException

class AppResultTest {

    @Test
    fun `getOrNull returns data for success`() {
        val result: AppResult<Int> = AppResult.Success(7)
        assertThat(result.getOrNull()).isEqualTo(7)
    }

    @Test
    fun `getOrNull returns null for error`() {
        val result: AppResult<Int> = AppResult.Error(AppError.Network)
        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `toAppError unwraps AppException`() {
        val error = AppException(AppError.NotFound).toAppError()
        assertThat(error).isEqualTo(AppError.NotFound)
    }

    @Test
    fun `toAppError maps IOException to network`() {
        assertThat(IOException("offline").toAppError()).isEqualTo(AppError.Network)
    }
}
