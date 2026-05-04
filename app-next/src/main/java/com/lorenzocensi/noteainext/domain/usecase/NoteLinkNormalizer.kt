package com.lorenzocensi.noteainext.domain.usecase

object NoteLinkNormalizer {
    fun normalizedId(firstNoteId: String, secondNoteId: String): String {
        require(firstNoteId != secondNoteId) { "A note cannot be linked to itself." }
        val (a, b) = ordered(firstNoteId, secondNoteId)
        return "$a:$b"
    }

    fun ordered(firstNoteId: String, secondNoteId: String): Pair<String, String> {
        require(firstNoteId != secondNoteId) { "A note cannot be linked to itself." }
        return if (firstNoteId < secondNoteId) firstNoteId to secondNoteId else secondNoteId to firstNoteId
    }
}
