package com.example.contentapp.di
import com.example.contentapp.data.repository.ContentRepository
import com.example.contentapp.data.repository.ContentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindContentRepository(contentRepositoryImpl: ContentRepositoryImpl): ContentRepository
}