package com.lorenzocensi.noteai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.crypto.tink.Aead
import com.lorenzocensi.noteai.data.security.TinkAead
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideAead(@ApplicationContext ctx: Context): Aead = TinkAead.getOrCreate(ctx)

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext ctx: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { ctx.preferencesDataStoreFile("noteai_secure") }
    )
}
