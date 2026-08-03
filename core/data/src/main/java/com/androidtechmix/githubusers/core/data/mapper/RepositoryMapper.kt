package com.androidtechmix.githubusers.core.data.mapper

import com.androidtechmix.githubusers.core.database.entity.RepoEntity
import com.androidtechmix.githubusers.core.domain.model.Repository
import com.androidtechmix.githubusers.core.network.dto.RepositoryDto

fun RepositoryDto.toEntity(ownerLogin: String): RepoEntity = RepoEntity(
    id = id,
    ownerLogin = ownerLogin,
    name = name,
    fullName = fullName,
    description = description,
    htmlUrl = htmlUrl,
    language = language,
    stargazersCount = stargazersCount,
    forksCount = forksCount,
    updatedAt = updatedAt,
    isPrivate = isPrivate,
)

fun RepoEntity.toDomain(): Repository = Repository(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    htmlUrl = htmlUrl,
    language = language,
    stargazersCount = stargazersCount,
    forksCount = forksCount,
    updatedAt = updatedAt,
    isPrivate = isPrivate,
)
