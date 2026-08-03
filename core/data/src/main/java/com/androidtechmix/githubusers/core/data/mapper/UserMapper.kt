package com.androidtechmix.githubusers.core.data.mapper

import com.androidtechmix.githubusers.core.database.entity.FavoriteEntity
import com.androidtechmix.githubusers.core.database.entity.UserEntity
import com.androidtechmix.githubusers.core.model.User
import com.androidtechmix.githubusers.core.network.dto.UserDto

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type,
    score = score,
)

fun UserEntity.toDomain(isFavorite: Boolean = false): User = User(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type,
    score = score,
    isFavorite = isFavorite,
)

fun UserEntity.toFavoriteEntity(favoritedAt: Long): FavoriteEntity = FavoriteEntity(
    login = login,
    userId = id,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type,
    favoritedAt = favoritedAt,
)
