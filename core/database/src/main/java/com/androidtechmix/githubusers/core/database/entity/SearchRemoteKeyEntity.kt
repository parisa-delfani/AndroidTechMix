package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_remote_keys")
data class SearchRemoteKeyEntity(
    @PrimaryKey val query: String,
    @ColumnInfo(name = "next_page") val nextPage: Int?,
    @ColumnInfo(name = "prev_page") val prevPage: Int?,
)
