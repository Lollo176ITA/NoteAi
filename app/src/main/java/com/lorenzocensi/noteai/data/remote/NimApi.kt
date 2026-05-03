package com.lorenzocensi.noteai.data.remote

import com.lorenzocensi.noteai.data.remote.dto.ChatRequest
import com.lorenzocensi.noteai.data.remote.dto.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NimApi {

    @POST("v1/chat/completions")
    suspend fun chatCompletions(@Body req: ChatRequest): Response<ChatResponse>

    companion object {
        const val BASE_URL = "https://integrate.api.nvidia.com/"
        const val MODEL_NEMOTRON_3_SUPER = "nvidia/nemotron-3-super-120b-a12b"
    }
}
