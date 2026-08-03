package com.androidtechmix.githubusers.core.data.mapper

import com.androidtechmix.githubusers.core.database.entity.FavoriteEntity
import com.androidtechmix.githubusers.core.database.entity.UserDetailEntity
import com.androidtechmix.githubusers.core.model.Repository
import com.androidtechmix.githubusers.core.model.UserDetail
import com.androidtechmix.githubusers.core.network.dto.UserDetailDto

fun UserDetailDto.toEntity(updatedAtLocal: Long): UserDetailEntity = UserDetailEntity(
    login = login,
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    bio = bio,
    company = company,
    location = location,
    blog = blog,
    twitterUsername = twitterUsername,
    publicRepos = publicRepos,
    publicGists = publicGists,
    followers = followers,
    following = following,
    createdAt = createdAt,
    updatedAtLocal = updatedAtLocal,
)

fun UserDetailEntity.toDomain(
    isFavorite: Boolean,
    repositories: List<Repository>,
): UserDetail = UserDetail(
    id = id,
    login = login,
    name = name,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    bio = bio,
    company = company,
    location = location,
    blog = blog,
    twitterUsername = twitterUsername,
    publicRepos = publicRepos,
    publicGists = publicGists,
    followers = followers,
    following = following,
    createdAt = createdAt,
    isFavorite = isFavorite,
    repositories = repositories,
)

fun UserDetailEntity.toFavoriteEntity(favoritedAt: Long): FavoriteEntity = FavoriteEntity(
    login = login,
    userId = id,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = "User",
    favoritedAt = favoritedAt,
)
