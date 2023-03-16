package com.bumble.appyx.interactions.transitionmodel.backstack

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.setupInteractionModel
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.operation.pop
import com.bumble.appyx.transitionmodel.backstack.operation.push
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BackStackSliderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var backStack: BackStack<NavTarget>

    @Test
    fun backStack_with_animations_enabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStackSlider(disableAnimations = false)
        composeTestRule.setupInteractionModel(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // all operations finished. advanced time > 2000 (last operation animation spec)
        composeTestRule.mainClock.advanceTimeBy(2100)

        assertEquals(3, backStack.availableElements().value.size)
    }


    @Test
    fun backStack_with_animations_enabled_does_not_clean_up_in_keyframe_mode_when_element_is_used() {
        createBackStackSlider(disableAnimations = false)
        composeTestRule.setupInteractionModel(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // last operation is not finished.  advanced time < 2000 (last operation animation spec)
        composeTestRule.mainClock.advanceTimeBy(1900)

        assertEquals(4, backStack.availableElements().value.size)
    }

    @Test
    fun backStack_with_animations_disabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStackSlider(disableAnimations = true)
        composeTestRule.setupInteractionModel(backStack)

        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop()

        assertEquals(3, backStack.availableElements().value.size)
    }

    @Test
    fun backStack_with_animations_enabled_cleans_destroyed_element_in_immediate_mode_when_animation_finished() {
        createBackStackSlider(disableAnimations = false)
        composeTestRule.setupInteractionModel(backStack)

        val pushSpringSpec = spring<Float>()
        val popSpringSpec = spring<Float>(stiffness = 10f)

        backStack.push(
            interactionTarget = NavTarget.Child2,
            mode = Operation.Mode.IMMEDIATE,
            animationSpec = pushSpringSpec
        )
        // all subsequent operations will be in IMMEDIATE mode until settled
        backStack.push(interactionTarget = NavTarget.Child3, animationSpec = pushSpringSpec)
        backStack.push(interactionTarget = NavTarget.Child4, animationSpec = pushSpringSpec)
        backStack.pop(animationSpec = popSpringSpec)

        // move clock until animations started
        composeTestRule.mainClock.advanceTimeUntil {
            backStack.isAnimating.value
        }

        // wait until animations are finished
        composeTestRule.mainClock.advanceTimeUntil {
            !backStack.isAnimating.value
        }


        assertEquals(3, backStack.availableElements().value.size)
    }

    private fun createBackStackSlider(disableAnimations: Boolean) {
        backStack = BackStack(
            model = BackStackModel(
                initialTargets = listOf(NavTarget.Child1),
                savedStateMap = null
            ),
            motionController = { BackStackSlider(it) },
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations
        )
    }
}
