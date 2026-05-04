package com.lorenzocensi.noteainext.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lorenzocensi.noteainext.data.db.AppNextDatabase
import com.lorenzocensi.noteainext.data.db.entity.NoteEntity
import com.lorenzocensi.noteainext.data.db.entity.NoteLinkEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNextDatabaseTest {
    private lateinit var db: AppNextDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppNextDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun searchFindsNotesByTitleAndBody() = runBlocking {
        db.noteDao().upsert(note("a", title = "Casa", body = "Lista spesa"))
        db.noteDao().upsert(note("b", title = "Studio", body = "Libro Kotlin"))

        assertEquals(listOf("a"), db.noteDao().observeFiltered("spesa", null).first().map { it.id })
        assertEquals(listOf("b"), db.noteDao().observeFiltered("studio", null).first().map { it.id })
    }

    @Test
    fun linksAreStoredAsSingleNormalizedRows() = runBlocking {
        db.noteDao().upsert(note("a"))
        db.noteDao().upsert(note("b"))
        db.noteLinkDao().upsert(NoteLinkEntity("a:b", "a", "b", "correlate", 3L))
        db.noteLinkDao().upsert(NoteLinkEntity("a:b", "a", "b", "aggiornate", 4L))

        val links = db.noteLinkDao().observeAll().first()

        assertEquals(1, links.size)
        assertEquals("aggiornate", links.single().label)
    }

    private fun note(
        id: String,
        title: String = "",
        body: String = ""
    ) = NoteEntity(
        id = id,
        projectId = null,
        title = title,
        body = body,
        createdAt = 1L,
        updatedAt = 1L
    )
}
