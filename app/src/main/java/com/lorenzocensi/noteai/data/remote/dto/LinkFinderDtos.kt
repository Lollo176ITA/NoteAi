package com.lorenzocensi.noteai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkFinderResult(
    val links: List<SuggestedLinkDto> = emptyList()
)

@Serializable
data class SuggestedLinkDto(
    @SerialName("target_id") val targetId: String,
    val reason: String,
    val score: Float? = null
)
