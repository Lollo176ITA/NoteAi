package com.lorenzocensi.noteainext.di

import android.content.Context
import androidx.room.Room
import com.lorenzocensi.noteainext.data.db.AppNextDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): AppNextDatabase =
        Room.databaseBuilder(context, AppNextDatabase::class.java, "noteai_next.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideProjectDao(db: AppNextDatabase) = db.projectDao()

    @Provides
    fun provideNoteDao(db: AppNextDatabase) = db.noteDao()

    @Provides
    fun provideNoteLinkDao(db: AppNextDatabase) = db.noteLinkDao()
}
