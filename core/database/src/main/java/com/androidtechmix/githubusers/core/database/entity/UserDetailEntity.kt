package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_details",
    indices = [Index(value = ["id"], unique = true)],
)
data class UserDetailEntity(
    @PrimaryKey val login: String,
    val id: Long,
    val name: String?,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    val bio: String?,
    val company: String?,
    val location: String?,
    val blog: String?,
    @ColumnInfo(name = "twitter_username") val twitterUsername: String?,
    @ColumnInfo(name = "public_repos") val publicRepos: Int,
    @ColumnInfo(name = "public_gists") val publicGists: Int,
    val followers: Int,
    val following: Int,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "updated_at_local") val updatedAtLocal: Long,
)
