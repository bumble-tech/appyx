package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.performClick
import com.bumble.appyx.testing.view.utils.DummyRoutingSource
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleView.Event
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.v2.sandbox.client.test.AppyxViewRule
import com.bumble.appyx.v2.sandbox.client.test.assertLastValueEqual
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
internal class MviCoreExampleViewTest {

    @get:Rule
    val composeTestRule: ComposeTestRule = createEmptyComposeRule()

    private val screen = MviCorePageObject(composeTestRule)

    @get:Rule
    val rule = AppyxViewRule(launchActivity = false) {
        MviCoreExampleView(backStack = DummyRoutingSource())
    }

    @Test
    fun GIVEN_loading_view_model_WHEN_displayed_THEN_loading_is_shown() {
        rule.start()
        rule.accept(ViewModel.Loading)

        with(screen) {
            loader.assertIsDisplayed()
        }
    }

    @Test
    fun GIVEN_initial_state_view_model_WHEN_displayed_THEN_loading_is_shown() {
        val initialText = "Initial State"
        rule.start()
        rule.accept(ViewModel.InitialState(initialText))

        with(screen) {
            initialStateText.assert(hasText(initialText))
            initialStateButtonTextTag.performClick()
        }


        rule.testEvents.assertLastValueEqual(Event.LoadDataClicked)
    }

}

