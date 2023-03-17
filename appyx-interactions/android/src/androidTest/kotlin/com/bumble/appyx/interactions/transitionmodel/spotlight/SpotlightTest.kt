package com.bumble.appyx.interactions.transitionmodel.spotlight

import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.setupInteractionModel
import com.bumble.appyx.interactions.waitUntilAnimationEnded
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.interpolator.SpotlightSlider
import com.bumble.appyx.transitionmodel.spotlight.operation.last
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SpotlightTest(private val testParam: TestParam) {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var spotlight: Spotlight<NavTarget>

    companion object {

        data class TestParam(
            val operationMode: Operation.Mode = Operation.Mode.IMMEDIATE
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data() = arrayOf(
            TestParam(operationMode = Operation.Mode.IMMEDIATE),
            TestParam(operationMode = Operation.Mode.KEYFRAME),
            TestParam(operationMode = Operation.Mode.GEOMETRY)
        )
    }

    @Test
    fun spotlight_calculates_visible_elements_correctly_when_clipToBounds_is_false() {
        createBackStackSlider(
            initialActiveIndex = 1f,
            clipToBounds = false
        )
        composeTestRule.setupInteractionModel(spotlight, 0.67f)
        checkInteractionTargetsOnScreen(setOf(NavTarget.Child1, NavTarget.Child2, NavTarget.Child3))

        if (testParam.operationMode == Operation.Mode.KEYFRAME) {
            val animationDuration = 1000
            spotlight.last(mode = testParam.operationMode, animationSpec = tween(animationDuration))
            composeTestRule.mainClock.advanceTimeBy(animationDuration.toLong())
        } else {
            spotlight.last(mode = testParam.operationMode)
            spotlight.waitUntilAnimationEnded(composeTestRule)
        }

        checkInteractionTargetsOnScreen(setOf(NavTarget.Child5, NavTarget.Child4))
    }

    @Test
    fun spotlight_calculates_visible_elements_correctly_when_clipToBounds_is_true() {
        createBackStackSlider(
            initialActiveIndex = 1f,
            clipToBounds = true
        )
        composeTestRule.setupInteractionModel(spotlight, 0.67f)

        if (testParam.operationMode == Operation.Mode.KEYFRAME) {
            val animationDuration = 1000
            spotlight.last(mode = testParam.operationMode, animationSpec = tween(animationDuration))
            composeTestRule.mainClock.advanceTimeBy(animationDuration.toLong())
        } else {
            spotlight.last(mode = testParam.operationMode)
            spotlight.waitUntilAnimationEnded(composeTestRule)
        }

        checkInteractionTargetsOnScreen(setOf(NavTarget.Child5))
    }

    private fun checkInteractionTargetsOnScreen(interactionTargets: Set<NavTarget>) {
        assertEquals(
            interactionTargets,
            spotlight.screenState.value.onScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun checkInteractionTargetsOffScreen(navTargets: Set<NavTarget>) {
        assertEquals(
            navTargets,
            spotlight.screenState.value.offScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun createBackStackSlider(
        disableAnimations: Boolean = false,
        initialActiveIndex: Float = 0f,
        clipToBounds: Boolean = false
    ) {
        spotlight = Spotlight(
            model = SpotlightModel(
                items = listOf(
                    NavTarget.Child1,
                    NavTarget.Child2,
                    NavTarget.Child3,
                    NavTarget.Child4,
                    NavTarget.Child5
                ),
                initialActiveIndex = initialActiveIndex
            ),
            motionController = { SpotlightSlider(uiContext = it, clipToBounds = clipToBounds) },
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations,
        )
    }
}
