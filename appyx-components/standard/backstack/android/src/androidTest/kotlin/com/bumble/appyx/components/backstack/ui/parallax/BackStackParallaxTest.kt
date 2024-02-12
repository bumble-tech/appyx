package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.interactions.model.transition.Update
import com.bumble.appyx.interactions.testing.TestTarget
import com.bumble.appyx.interactions.testing.setupAppyxComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class BackStackParallaxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var backStack: BackStack<TestTarget>
    private lateinit var backStackModel: BackStackModel<TestTarget>
    private lateinit var visualisation: BackStackParallax<TestTarget>

    @Test
    fun backStackParallax_resolves_visibility_to_false_when_element_is_not_top_most_stashed_one() {
        createBackStack()
        composeTestRule.setupAppyxComponent(backStack)

        backStack.push(navTarget = TestTarget.Child2)
        backStack.push(navTarget = TestTarget.Child3)
        backStack.push(navTarget = TestTarget.Child4)

        composeTestRule.waitForIdle()

        with(visualisation.mapUpdate(backStackModel.output.value as Update<BackStackModel.State<TestTarget>>)) {
            Assert.assertFalse(get(0).visibleState.value) // Child #1 should be false
            Assert.assertFalse(get(1).visibleState.value) // Child #2 should be false
            Assert.assertFalse(get(2).visibleState.value)  // Child #3 should be false
            Assert.assertTrue(get(3).visibleState.value)  // Child #4 should be true
        }
    }

    private fun createBackStack() {
        backStackModel = BackStackModel(
            initialTargets = listOf(TestTarget.Child1),
            savedStateMap = null
        )
        backStack = BackStack(
            model = backStackModel,
            visualisation = { uiContext ->
                BackStackParallax<TestTarget>(uiContext).also {
                    visualisation = it
                }
            },
            scope = CoroutineScope(Dispatchers.Unconfined),
        )
    }

}
