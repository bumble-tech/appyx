package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.bumble.appyx.testing.ui.utils.DummyRoutingSource
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.client.test.appyxParentViewRule
import com.bumble.appyx.sandbox.client.test.assertLastValueEqual
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
internal class MviCoreExampleViewTest {

    private var title = "Title"

    @get:Rule
    val rule = appyxParentViewRule(launchActivity = false) {
        createView()
    }

    private val screen = MviCorePageObject(rule)

    @Test
    fun GIVEN_loading_view_model_WHEN_displayed_THEN_loading_is_shown() {
        title = "Modified title"
        rule.start()
        rule.accept(ViewModel.Loading)

        with(screen) {
            titleText.assert(hasText(title))
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
            initialStateButtonText.performClick()
        }


        rule.testEvents.assertLastValueEqual(Event.LoadDataClicked)
    }

    private fun createView() = MviCoreExampleViewImpl(
        title = title,
        backStack = DummyRoutingSource()
    )

}

