package com.bumble.appyx.components.spotlight

import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.testing.NavTarget
import com.bumble.appyx.interactions.testing.setupInteractionModel
import com.bumble.appyx.interactions.testing.waitUntilAnimationEnded
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
        createSpotlightSlider(initialActiveIndex = 1f)
        composeTestRule.setupInteractionModel(spotlight, 0.67f, clipToBounds = false)
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
        createSpotlightSlider(initialActiveIndex = 1f)
        composeTestRule.setupInteractionModel(spotlight, 0.67f, clipToBounds = true)

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
            spotlight.elements.value.onScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun checkInteractionTargetsOffScreen(navTargets: Set<NavTarget>) {
        assertEquals(
            navTargets,
            spotlight.elements.value.offScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun createSpotlightSlider(
        disableAnimations: Boolean = false,
        initialActiveIndex: Float = 0f,
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
                initialActiveIndex = initialActiveIndex,
                savedStateMap = null
            ),
            motionController = { SpotlightSlider(uiContext = it) },
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations,
        )
    }
}