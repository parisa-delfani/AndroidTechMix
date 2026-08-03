package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repositories",
    foreignKeys = [
        ForeignKey(
            entity = UserDetailEntity::class,
            parentColumns = ["login"],
            childColumns = ["owner_login"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("owner_login")],
)
data class RepoEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "owner_login") val ownerLogin: String,
    val name: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    val description: String?,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    val language: String?,
    @ColumnInfo(name = "stargazers_count") val stargazersCount: Int,
    @ColumnInfo(name = "forks_count") val forksCount: Int,
    @ColumnInfo(name = "updated_at") val updatedAt: String?,
    @ColumnInfo(name = "is_private") val isPrivate: Boolean,
)
