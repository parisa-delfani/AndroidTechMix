package com.androidtechmix.githubusers.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.androidtechmix.githubusers.core.database.entity.SearchRemoteKeyEntity
import com.androidtechmix.githubusers.core.database.entity.SearchResultEntity
import com.androidtechmix.githubusers.core.database.entity.UserEntity

@Dao
interface SearchDao {
    @Query(
        """
        SELECT u.* FROM users AS u
        INNER JOIN search_results AS s ON s.user_id = u.id
        WHERE s.query = :query
        ORDER BY s.page_index ASC, s.item_index ASC
        """,
    )
    fun pagingSource(query: String): PagingSource<Int, UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(results: List<SearchResultEntity>)

    @Query("DELETE FROM search_results WHERE query = :query")
    suspend fun clearSearchResults(query: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRemoteKey(key: SearchRemoteKeyEntity)

    @Query("SELECT * FROM search_remote_keys WHERE query = :query")
    suspend fun remoteKey(query: String): SearchRemoteKeyEntity?

    @Query("DELETE FROM search_remote_keys WHERE query = :query")
    suspend fun clearRemoteKey(query: String)

    @Transaction
    suspend fun clearSearch(query: String) {
        clearSearchResults(query)
        clearRemoteKey(query)
    }
}
