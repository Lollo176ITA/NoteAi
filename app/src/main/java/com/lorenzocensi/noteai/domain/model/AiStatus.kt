package com.lorenzocensi.noteai.domain.model

sealed interface AiStatus {
    data object Idle : AiStatus
    data class Pending(val secondsLeft: Int) : AiStatus
    data object Running : AiStatus
    data object MissingApiKey : AiStatus
    data class Error(val reason: String) : AiStatus
    data class Done(val linkCount: Int) : AiStatus
}
