package com.bumble.appyx.components.backstack

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackstackFader
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.testing.NavTarget
import com.bumble.appyx.interactions.testing.setupInteractionModel
import com.bumble.appyx.interactions.testing.waitUntilAnimationEnded
import com.bumble.appyx.interactions.testing.waitUntilAnimationStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class BackStackTest(private val testParam: TestParam) {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var backStack: BackStack<NavTarget>

    companion object {
        data class TestParam(
            val motionController: (UiContext) -> MotionController<NavTarget, BackStackModel.State<NavTarget>>
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data() = arrayOf(
            TestParam(motionController = { BackStackSlider(it) }),
            TestParam(motionController = { BackstackFader(it) }),
        )
    }

    @Test
    fun backStack_with_animations_enabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStack(disableAnimations = false, testParam.motionController)
        composeTestRule.setupInteractionModel(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // all operations finished. advanced time > 2000 (last operation animation spec)
        composeTestRule.mainClock.advanceTimeBy(2100)

        assertEquals(3, backStack.elements.value.all.size)
    }


    @Test
    fun backStack_with_animations_enabled_does_not_clean_up_in_keyframe_mode_when_element_is_used() {
        createBackStack(disableAnimations = false, testParam.motionController)
        composeTestRule.setupInteractionModel(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // last operation is not finished.  advanced time < 2000 (last operation animation spec)
        composeTestRule.mainClock.advanceTimeBy(1900)

        assertEquals(4, backStack.elements.value.all.size)
    }

    @Test
    fun backStack_with_animations_disabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStack(disableAnimations = true, testParam.motionController)
        composeTestRule.setupInteractionModel(backStack)

        backStack.push(interactionTarget = NavTarget.Child2)
        backStack.push(interactionTarget = NavTarget.Child3)
        backStack.push(interactionTarget = NavTarget.Child4)
        backStack.pop()

        assertEquals(3, backStack.elements.value.all.size)
    }

    @Test
    fun backStack_with_animations_enabled_cleans_destroyed_element_in_immediate_mode_when_animation_finished() {
        createBackStack(disableAnimations = false, testParam.motionController)
        composeTestRule.setupInteractionModel(backStack)

        val pushSpringSpec = spring<Float>()
        val popSpringSpec = spring<Float>(stiffness = 10f)

        backStack.push(
            interactionTarget = NavTarget.Child2,
            mode = Operation.Mode.IMMEDIATE,
            animationSpec = pushSpringSpec
        )
        // all subsequent operations will be in IMMEDIATE mode until settled
        backStack.push(
            interactionTarget = NavTarget.Child3,
            animationSpec = pushSpringSpec
        )
        backStack.push(
            interactionTarget = NavTarget.Child4,
            animationSpec = pushSpringSpec
        )
        backStack.pop(animationSpec = popSpringSpec)

        // move clock until animations started
        backStack.waitUntilAnimationStarted(composeTestRule)

        // wait until animations are finished
        backStack.waitUntilAnimationEnded(composeTestRule)


        assertEquals(3, backStack.elements.value.all.size)
    }

    private fun createBackStack(
        disableAnimations: Boolean,
        motionController: (UiContext) -> MotionController<NavTarget, BackStackModel.State<NavTarget>>
    ) {
        backStack = BackStack(
            model = BackStackModel(
                initialTargets = listOf(NavTarget.Child1),
                savedStateMap = null
            ),
            motionController = motionController,
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations
        )
    }
}
