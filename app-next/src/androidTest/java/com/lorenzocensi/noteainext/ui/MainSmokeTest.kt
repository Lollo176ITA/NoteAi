package com.lorenzocensi.noteainext.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.lorenzocensi.noteainext.MainActivity
import org.junit.Rule
import org.junit.Test

class MainSmokeTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationShowsPrimarySections() {
        compose.onNodeWithText("Note").assertIsDisplayed()
        compose.onNodeWithText("Progetti").assertIsDisplayed()
        compose.onNodeWithText("Link").assertIsDisplayed()
    }
}
