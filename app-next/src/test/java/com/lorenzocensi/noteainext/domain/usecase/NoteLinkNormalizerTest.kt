package com.lorenzocensi.noteainext.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class NoteLinkNormalizerTest {
    @Test
    fun normalizedIdIsStableForBothDirections() {
        assertEquals(
            NoteLinkNormalizer.normalizedId("a", "z"),
            NoteLinkNormalizer.normalizedId("z", "a")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun selfLinksAreRejected() {
        NoteLinkNormalizer.normalizedId("same", "same")
    }
}
