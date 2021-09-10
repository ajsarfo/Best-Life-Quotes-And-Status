package com.sarftec.lifequotesandstatus.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class Injection {

    @Binds
    @Singleton
    abstract fun repository(repository: RepositoryImpl) : Repository
}