package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["login"], unique = true)],
)
data class UserEntity(
    @PrimaryKey val id: Long,
    val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    val type: String,
    val score: Double? = null,
)
