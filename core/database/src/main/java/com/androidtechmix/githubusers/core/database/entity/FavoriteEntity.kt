package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val login: String,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    val type: String,
    @ColumnInfo(name = "favorited_at") val favoritedAt: Long,
)
