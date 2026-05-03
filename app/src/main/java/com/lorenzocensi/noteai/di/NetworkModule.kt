package com.lorenzocensi.noteai.di

import com.lorenzocensi.noteai.data.remote.NimApi
import com.lorenzocensi.noteai.data.remote.NimClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNimApi(factory: NimClientFactory): NimApi = factory.create()

    @Provides
    @Singleton
    fun provideJson(factory: NimClientFactory): Json = factory.json
}
