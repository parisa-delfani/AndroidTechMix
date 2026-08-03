package com.androidtechmix.githubusers.core.data.di

import com.androidtechmix.githubusers.core.common.dispatcher.DefaultDispatcher
import com.androidtechmix.githubusers.core.common.dispatcher.IoDispatcher
import com.androidtechmix.githubusers.core.common.dispatcher.MainDispatcher
import com.androidtechmix.githubusers.core.data.repository.UserRepositoryImpl
import com.androidtechmix.githubusers.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}
