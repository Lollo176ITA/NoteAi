package com.lorenzocensi.noteainext.domain.usecase

import com.lorenzocensi.noteainext.domain.model.SaveStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutosaveControllerTest {
    @Test
    fun savesOnlyLatestScheduledValueAfterDebounce() = runTest {
        val saved = mutableListOf<String>()
        val controller = AutosaveController<String>(
            scope = this,
            delayMillis = 100L,
            save = { value -> saved += value }
        )

        controller.schedule("prima")
        advanceTimeBy(50L)
        controller.schedule("seconda")
        advanceTimeBy(99L)
        runCurrent()

        assertEquals(emptyList<String>(), saved)

        advanceTimeBy(1L)
        runCurrent()

        assertEquals(listOf("seconda"), saved)
        assertEquals(SaveStatus.Saved, controller.status.value)
    }
}
