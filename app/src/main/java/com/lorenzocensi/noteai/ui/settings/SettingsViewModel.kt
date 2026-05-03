package com.lorenzocensi.noteai.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteai.data.remote.NimApi
import com.lorenzocensi.noteai.data.remote.dto.ChatMessage
import com.lorenzocensi.noteai.data.remote.dto.ChatRequest
import com.lorenzocensi.noteai.data.security.ApiKeyStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VerifyState {
    data object Idle : VerifyState()
    data object Verifying : VerifyState()
    data object Ok : VerifyState()
    data class Error(val message: String) : VerifyState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: ApiKeyStore,
    private val nimApi: NimApi
) : ViewModel() {

    private val _key = MutableStateFlow("")
    val key: StateFlow<String> = _key.asStateFlow()

    private val _verify = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val verify: StateFlow<VerifyState> = _verify.asStateFlow()

    init {
        viewModelScope.launch { _key.value = store.getKey().orEmpty() }
    }

    fun onKeyChange(v: String) { _key.value = v }

    fun saveKey() {
        viewModelScope.launch {
            store.setKey(_key.value)
            _verify.value = VerifyState.Idle
        }
    }

    fun verifyKey() {
        viewModelScope.launch {
            _verify.value = VerifyState.Verifying
            store.setKey(_key.value)
            val result = runCatching {
                nimApi.chatCompletions(
                    ChatRequest(
                        model = NimApi.MODEL_NEMOTRON_3_SUPER,
                        messages = listOf(ChatMessage(role = "user", content = "ping")),
                        maxTokens = 8
                    )
                )
            }
            _verify.value = result.fold(
                onSuccess = { resp ->
                    if (resp.isSuccessful) VerifyState.Ok
                    else VerifyState.Error("HTTP ${resp.code()}")
                },
                onFailure = { VerifyState.Error(it.message ?: "errore di rete") }
            )
        }
    }
}
