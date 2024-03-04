package com.example.racepal.repositories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(combinedUserRepository: CombinedUserRepository): UserRepository

    @Binds
    abstract fun bindRunRepository(combinedRunRepository: CombinedRunRepository): RunRepository
}