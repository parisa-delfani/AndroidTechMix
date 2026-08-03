package com.androidtechmix.githubusers.di

import com.androidtechmix.githubusers.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @Named("github_token")
    fun provideGitHubToken(): String = BuildConfig.GITHUB_TOKEN
}
