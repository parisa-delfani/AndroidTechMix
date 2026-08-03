package com.androidtechmix.githubusers.core.database.di

import android.content.Context
import androidx.room.Room
import com.androidtechmix.githubusers.core.database.GitHubDatabase
import com.androidtechmix.githubusers.core.database.dao.FavoriteDao
import com.androidtechmix.githubusers.core.database.dao.SearchDao
import com.androidtechmix.githubusers.core.database.dao.UserDao
import com.androidtechmix.githubusers.core.database.dao.UserDetailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): GitHubDatabase = Room.databaseBuilder(
        context,
        GitHubDatabase::class.java,
        "github_users.db",
    ).fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideUserDao(db: GitHubDatabase): UserDao = db.userDao()

    @Provides
    fun provideSearchDao(db: GitHubDatabase): SearchDao = db.searchDao()

    @Provides
    fun provideUserDetailDao(db: GitHubDatabase): UserDetailDao = db.userDetailDao()

    @Provides
    fun provideFavoriteDao(db: GitHubDatabase): FavoriteDao = db.favoriteDao()
}
