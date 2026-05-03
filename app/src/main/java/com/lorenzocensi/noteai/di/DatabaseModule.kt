package com.lorenzocensi.noteai.di

import android.content.Context
import androidx.room.Room
import com.lorenzocensi.noteai.data.db.AppDatabase
import com.lorenzocensi.noteai.data.db.dao.ConnectionDao
import com.lorenzocensi.noteai.data.db.dao.NoteDao
import com.lorenzocensi.noteai.data.db.dao.ProjectDao
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
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "noteai.db")
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .build()

    @Provides fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides fun provideConnectionDao(db: AppDatabase): ConnectionDao = db.connectionDao()
}
