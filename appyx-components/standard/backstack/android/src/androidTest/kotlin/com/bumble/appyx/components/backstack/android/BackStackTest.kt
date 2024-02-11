package com.bumble.appyx.components.backstack.android

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.testing.InteractionTarget
import com.bumble.appyx.interactions.testing.setupAppyxComponent
import com.bumble.appyx.interactions.testing.waitUntilAnimationEnded
import com.bumble.appyx.interactions.testing.waitUntilAnimationStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class BackStackTest(private val testParam: TestParam) {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var backStack: BackStack<InteractionTarget>

    companion object {
        data class TestParam(
            val visualisation: (UiContext) -> Visualisation<InteractionTarget, BackStackModel.State<InteractionTarget>>
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data() = arrayOf(
            TestParam(visualisation = { BackStackSlider(it) }),
            TestParam(visualisation = { BackStackFader(it) }),
            TestParam(visualisation = { BackStackParallax(it) }),
            TestParam(visualisation = { BackStack3D(it) }),
        )
    }

    @Test
    fun backStack_with_animations_enabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStack(disableAnimations = false, testParam.visualisation)
        composeTestRule.setupAppyxComponent(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = InteractionTarget.Child2)
        backStack.push(interactionTarget = InteractionTarget.Child3)
        backStack.push(interactionTarget = InteractionTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // all operations finished
        composeTestRule.waitForIdle()

        Assert.assertEquals(3, backStack.elements.value.all.size)
    }


    @Test
    fun backStack_with_animations_enabled_does_not_clean_up_in_keyframe_mode_when_element_is_used() {
        createBackStack(disableAnimations = false, testParam.visualisation)
        composeTestRule.setupAppyxComponent(backStack)

        val tweenTwoSec = tween<Float>(durationMillis = 2000)
        backStack.push(interactionTarget = InteractionTarget.Child2)
        backStack.push(interactionTarget = InteractionTarget.Child3)
        backStack.push(interactionTarget = InteractionTarget.Child4)
        backStack.pop(animationSpec = tweenTwoSec)

        // last operation is not finished.  advanced time < 2000 (last operation animation spec)
        composeTestRule.mainClock.advanceTimeBy(1900)

        Assert.assertEquals(4, backStack.elements.value.all.size)
    }

    @Test
    fun backStack_with_animations_disabled_cleans_up_destroyed_element_in_keyframe_mode_when_settled() {
        createBackStack(disableAnimations = true, testParam.visualisation)
        composeTestRule.setupAppyxComponent(backStack)

        backStack.push(interactionTarget = InteractionTarget.Child2)
        backStack.push(interactionTarget = InteractionTarget.Child3)
        backStack.push(interactionTarget = InteractionTarget.Child4)
        backStack.pop()

        Assert.assertEquals(3, backStack.elements.value.all.size)
    }

    @Test
    fun backStack_with_animations_enabled_cleans_destroyed_element_in_immediate_mode_when_animation_finished() {
        createBackStack(disableAnimations = false, testParam.visualisation)
        composeTestRule.setupAppyxComponent(backStack)

        val pushSpringSpec = spring<Float>()
        val popSpringSpec = spring<Float>(stiffness = 10f)

        backStack.push(
            interactionTarget = InteractionTarget.Child2,
            mode = Operation.Mode.IMMEDIATE,
            animationSpec = pushSpringSpec
        )
        // all subsequent operations will be in IMMEDIATE mode until settled
        backStack.push(
            interactionTarget = InteractionTarget.Child3,
            animationSpec = pushSpringSpec
        )
        backStack.push(
            interactionTarget = InteractionTarget.Child4,
            animationSpec = pushSpringSpec
        )
        backStack.pop(animationSpec = popSpringSpec)

        // move clock until animations started
        backStack.waitUntilAnimationStarted(composeTestRule)

        // wait until animations are finished
        backStack.waitUntilAnimationEnded(composeTestRule)


        Assert.assertEquals(3, backStack.elements.value.all.size)
    }

    private fun createBackStack(
        disableAnimations: Boolean,
        visualisation: (UiContext) -> Visualisation<InteractionTarget, BackStackModel.State<InteractionTarget>>
    ) {
        backStack = BackStack(
            model = BackStackModel(
                initialTargets = listOf(InteractionTarget.Child1),
                savedStateMap = null
            ),
            visualisation = visualisation,
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations
        )
    }
}
