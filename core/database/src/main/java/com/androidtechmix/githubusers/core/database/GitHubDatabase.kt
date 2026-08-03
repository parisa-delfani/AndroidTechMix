package com.androidtechmix.githubusers.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androidtechmix.githubusers.core.database.dao.FavoriteDao
import com.androidtechmix.githubusers.core.database.dao.SearchDao
import com.androidtechmix.githubusers.core.database.dao.UserDao
import com.androidtechmix.githubusers.core.database.dao.UserDetailDao
import com.androidtechmix.githubusers.core.database.entity.FavoriteEntity
import com.androidtechmix.githubusers.core.database.entity.RepoEntity
import com.androidtechmix.githubusers.core.database.entity.SearchRemoteKeyEntity
import com.androidtechmix.githubusers.core.database.entity.SearchResultEntity
import com.androidtechmix.githubusers.core.database.entity.UserDetailEntity
import com.androidtechmix.githubusers.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        UserDetailEntity::class,
        RepoEntity::class,
        FavoriteEntity::class,
        SearchResultEntity::class,
        SearchRemoteKeyEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class GitHubDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun searchDao(): SearchDao
    abstract fun userDetailDao(): UserDetailDao
    abstract fun favoriteDao(): FavoriteDao
}
