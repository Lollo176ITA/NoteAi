package com.lorenzocensi.noteainext.data.repository

import com.lorenzocensi.noteainext.data.db.dao.NoteDao
import com.lorenzocensi.noteainext.data.db.dao.NoteLinkDao
import com.lorenzocensi.noteainext.domain.model.NoteLink
import com.lorenzocensi.noteainext.domain.model.NoteLinkPreview
import com.lorenzocensi.noteainext.domain.usecase.NoteLinkNormalizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteLinkRepository @Inject constructor(
    private val linkDao: NoteLinkDao,
    private val noteDao: NoteDao
) {
    fun observeAll(): Flow<List<NoteLink>> = linkDao.observeAll().map { items -> items.map { it.toDomain() } }

    fun observeForNote(noteId: String): Flow<List<NoteLink>> =
        linkDao.observeForNote(noteId).map { items -> items.map { it.toDomain() } }

    fun observePreviews(): Flow<List<NoteLinkPreview>> =
        combine(linkDao.observeAll(), noteDao.observeAll()) { links, notes ->
            val titles = notes.associate { it.id to displayTitle(it.title, it.body) }
            links.map { link ->
                val domain = link.toDomain()
                NoteLinkPreview(
                    link = domain,
                    noteATitle = titles[domain.noteAId] ?: "Nota eliminata",
                    noteBTitle = titles[domain.noteBId] ?: "Nota eliminata"
                )
            }
        }

    suspend fun linkNotes(firstNoteId: String, secondNoteId: String, label: String?) {
        val (a, b) = NoteLinkNormalizer.ordered(firstNoteId, secondNoteId)
        val link = NoteLink(
            id = NoteLinkNormalizer.normalizedId(a, b),
            noteAId = a,
            noteBId = b,
            label = label?.trim()?.takeIf { it.isNotBlank() },
            createdAt = System.currentTimeMillis()
        )
        linkDao.upsert(link.toEntity())
    }

    suspend fun delete(link: NoteLink) = linkDao.delete(link.toEntity())

    private fun displayTitle(title: String, body: String): String =
        title.ifBlank { body.lineSequence().firstOrNull().orEmpty() }.ifBlank { "Senza titolo" }
}
