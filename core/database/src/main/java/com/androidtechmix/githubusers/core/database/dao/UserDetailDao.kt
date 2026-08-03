package com.androidtechmix.githubusers.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.androidtechmix.githubusers.core.database.entity.RepoEntity
import com.androidtechmix.githubusers.core.database.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDetail(detail: UserDetailEntity)

    @Query("SELECT * FROM user_details WHERE login = :login")
    fun observeDetail(login: String): Flow<UserDetailEntity?>

    @Query("SELECT * FROM user_details WHERE login = :login")
    suspend fun getDetail(login: String): UserDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRepos(repos: List<RepoEntity>)

    @Query("DELETE FROM repositories WHERE owner_login = :login")
    suspend fun clearRepos(login: String)

    @Query("SELECT * FROM repositories WHERE owner_login = :login ORDER BY stargazers_count DESC")
    fun observeRepos(login: String): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repositories WHERE owner_login = :login ORDER BY stargazers_count DESC")
    suspend fun getRepos(login: String): List<RepoEntity>

    @Transaction
    suspend fun upsertDetailWithRepos(detail: UserDetailEntity, repos: List<RepoEntity>) {
        upsertDetail(detail)
        clearRepos(detail.login)
        if (repos.isNotEmpty()) {
            upsertRepos(repos)
        }
    }
}
