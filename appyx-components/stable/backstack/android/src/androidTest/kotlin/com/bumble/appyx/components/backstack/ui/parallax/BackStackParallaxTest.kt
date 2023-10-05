package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.testing.InteractionTarget
import com.bumble.appyx.interactions.testing.setupAppyxComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class BackStackParallaxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var backStack: BackStack<InteractionTarget>
    private lateinit var backStackModel: BackStackModel<InteractionTarget>
    private lateinit var visualisation: BackStackParallax<InteractionTarget>

    @Test
    fun backStackParallax_resolves_visibility_to_false_when_element_is_not_top_most_stashed_one() {
        createBackStack()
        composeTestRule.setupAppyxComponent(backStack)

        backStack.push(interactionTarget = InteractionTarget.Child2)
        backStack.push(interactionTarget = InteractionTarget.Child3)
        backStack.push(interactionTarget = InteractionTarget.Child4)

        composeTestRule.waitForIdle()

        with(visualisation.mapUpdate(backStackModel.output.value as Update<BackStackModel.State<InteractionTarget>>)) {
            Assert.assertFalse(get(0).visibleState.value) // Child #1 should be false
            Assert.assertFalse(get(1).visibleState.value) // Child #2 should be false
            Assert.assertTrue(get(2).visibleState.value)  // Child #3 should be true
            Assert.assertTrue(get(3).visibleState.value)  // Child #4 should be true
        }
    }

    private fun createBackStack() {
        backStackModel = BackStackModel(
            initialTargets = listOf(InteractionTarget.Child1),
            savedStateMap = null
        )
        backStack = BackStack(
            model = backStackModel,
            visualisation = { buildContext ->
                BackStackParallax<InteractionTarget>(buildContext).also {
                    visualisation = it
                }
            },
            scope = CoroutineScope(Dispatchers.Unconfined),
        )
    }

}
