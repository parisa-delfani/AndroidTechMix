package com.androidtechmix.githubusers.core.network.api

import com.androidtechmix.githubusers.core.network.dto.RepositoryDto
import com.androidtechmix.githubusers.core.network.dto.SearchUsersResponseDto
import com.androidtechmix.githubusers.core.network.dto.UserDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): SearchUsersResponseDto

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
    ): UserDetailDto

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
    ): List<RepositoryDto>
}
