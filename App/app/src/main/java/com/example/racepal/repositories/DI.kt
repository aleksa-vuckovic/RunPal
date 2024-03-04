package com.example.racepal.repositories

import com.example.racepal.repositories.run.CombinedRunRepository
import com.example.racepal.repositories.run.RunRepository
import com.example.racepal.repositories.user.CombinedUserRepository
import com.example.racepal.repositories.user.UserRepository
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