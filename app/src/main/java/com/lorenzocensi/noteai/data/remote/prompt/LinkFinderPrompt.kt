package com.lorenzocensi.noteai.data.remote.prompt

import com.lorenzocensi.noteai.data.db.dao.NoteSnippet
import com.lorenzocensi.noteai.data.db.entity.NoteEntity
import com.lorenzocensi.noteai.data.remote.dto.ChatMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object LinkFinderPrompt {

    private const val SYSTEM = """Sei un assistente che individua collegamenti concettuali tra le note di un utente.
Restituisci ESCLUSIVAMENTE un JSON valido con questa struttura, nessun testo aggiuntivo:
{"links": [{"target_id": "<id nota collegata>", "reason": "<spiegazione in italiano max 120 caratteri>", "score": <numero tra 0 e 1>}]}

Regole:
- Massimo 10 collegamenti, ordinati per pertinenza decrescente.
- Considera solo collegamenti realmente significativi, non superficiali.
- target_id DEVE essere uno degli id della lista "altre_note".
- Se non trovi collegamenti rilevanti, restituisci {"links": []}.
- NON inventare id non presenti."""

    fun build(currentNote: NoteEntity, others: List<NoteSnippet>, json: Json): List<ChatMessage> {
        val othersJson = JsonArray(
            others.map {
                JsonObject(
                    mapOf(
                        "id" to JsonPrimitive(it.id),
                        "title" to JsonPrimitive(it.title),
                        "snippet" to JsonPrimitive(it.body)
                    )
                )
            }
        )
        val current = JsonObject(
            mapOf(
                "id" to JsonPrimitive(currentNote.id),
                "title" to JsonPrimitive(currentNote.title),
                "body" to JsonPrimitive(currentNote.body)
            )
        )
        val payload = JsonObject(
            mapOf(
                "nota_corrente" to current,
                "altre_note" to othersJson
            )
        )
        return listOf(
            ChatMessage(role = "system", content = SYSTEM),
            ChatMessage(role = "user", content = json.encodeToString(payload))
        )
    }

    fun stripFences(raw: String): String {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("```")) return trimmed
        val withoutFirst = trimmed.removePrefix("```").let { s ->
            // remove optional language tag like "json"
            val nl = s.indexOf('\n')
            if (nl >= 0) s.substring(nl + 1) else s
        }
        return withoutFirst.removeSuffix("```").trim()
    }
}
