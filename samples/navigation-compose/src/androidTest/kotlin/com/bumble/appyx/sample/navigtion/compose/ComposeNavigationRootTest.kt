package com.bumble.appyx.sample.navigtion.compose

import androidx.annotation.CheckResult
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.testing.ui.rules.AppyxTestActivity
import org.junit.Rule
import org.junit.Test

class ComposeNavigationRootTest {
    @get:Rule
    internal val composeTestRule = createAndroidComposeRule<AppyxTestActivity>()

    init {
        AppyxTestActivity.composableView = { appyxTestActivity ->
            // 'integrationPoint' must be provided to ensure it can be accessed from within the
            // Jetpack compose navigation graph.
            CompositionLocalProvider(
                LocalIntegrationPoint provides appyxTestActivity.appyxIntegrationPoint,
            ) {
                ComposeNavigationRoot()
            }
        }
    }

    @Test
    fun WHEN_screen_loads_THEN_verify_initial_screen_state() {
        assertScreenTitleExists()
        onNodeWithGoogleSubtitle().assertExists()
        onNodeWithNavigationToAppyxText().assertExists()
    }

    @Test
    fun WHEN_navigate_to_appyx_clicked_THEN_verify_appyx_screen_state() {
        onNodeWithNavigationToAppyxText().performClick()

        assertScreenTitleExists()
        onNodeWithAppyxSubtitle().assertExists()
        onNodeWithNavigationToGoogleText().assertExists()
        onNodeWithGoogleSubtitle().assertDoesNotExist()
        onNodeWithNavigationToAppyxText().assertDoesNotExist()
    }

    @Test
    fun GIVEN_navigated_to_appyx_WHEN_navigate_to_google_clicked_THEN_verify_google_screen_state() {
        onNodeWithNavigationToAppyxText().performClick()

        onNodeWithNavigationToGoogleText().performClick()

        assertScreenTitleExists()
        onNodeWithGoogleSubtitle().assertExists()
        onNodeWithNavigationToAppyxText().assertExists()
        onNodeWithAppyxSubtitle().assertDoesNotExist()
        onNodeWithNavigationToGoogleText().assertDoesNotExist()
    }

    private fun assertScreenTitleExists() {
        composeTestRule.onNodeWithText("Navigation Compose interop example").assertExists()
    }

    @CheckResult
    private fun onNodeWithGoogleSubtitle() =
        composeTestRule.onNodeWithText("Google's Jetpack Navigation screen")

    @CheckResult
    private fun onNodeWithAppyxSubtitle() =
        composeTestRule.onNodeWithText("Appyx screen")

    @CheckResult
    private fun onNodeWithNavigationToGoogleText() =
        composeTestRule.onNodeWithText("Navigate to Google")

    @CheckResult
    private fun onNodeWithNavigationToAppyxText() =
        composeTestRule.onNodeWithText("Navigate to Appyx")
}
