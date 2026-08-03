package com.androidtechmix.githubusers.core.data.mapper

import com.androidtechmix.githubusers.core.database.entity.FavoriteEntity
import com.androidtechmix.githubusers.core.domain.model.User

fun FavoriteEntity.toDomain(): User = User(
    id = userId,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type,
    isFavorite = true,
)
