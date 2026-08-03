package com.androidtechmix.githubusers.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "search_results",
    primaryKeys = ["query", "user_id"],
    indices = [Index("query"), Index("user_id")],
)
data class SearchResultEntity(
    val query: String,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "page_index") val pageIndex: Int,
    @ColumnInfo(name = "item_index") val itemIndex: Int,
)
