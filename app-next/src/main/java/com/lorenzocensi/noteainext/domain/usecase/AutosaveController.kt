package com.lorenzocensi.noteainext.domain.usecase

import com.lorenzocensi.noteainext.domain.model.SaveStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutosaveController<T>(
    private val scope: CoroutineScope,
    private val delayMillis: Long = 700L,
    private val save: suspend (T) -> Unit
) {
    private val _status = MutableStateFlow(SaveStatus.Saved)
    val status: StateFlow<SaveStatus> = _status

    private var pendingJob: Job? = null

    fun schedule(value: T) {
        pendingJob?.cancel()
        _status.value = SaveStatus.Saving
        pendingJob = scope.launch {
            delay(delayMillis)
            runCatching { save(value) }
                .onSuccess { _status.value = SaveStatus.Saved }
                .onFailure { _status.value = SaveStatus.Error }
        }
    }

    fun cancel() {
        pendingJob?.cancel()
        pendingJob = null
    }
}
