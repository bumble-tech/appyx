package com.bumble.appyx.components.spotlight.android

import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.utils.testing.TestTarget
import com.bumble.appyx.interactions.utils.testing.setupAppyxComponent
import com.bumble.appyx.interactions.utils.testing.waitUntilAnimationEnded
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

    private lateinit var spotlight: Spotlight<TestTarget>

    companion object {

        data class TestParam(
            val operationMode: Operation.Mode = Operation.Mode.IMMEDIATE
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data() = arrayOf(
            TestParam(operationMode = Operation.Mode.IMMEDIATE),
            TestParam(operationMode = Operation.Mode.KEYFRAME),
            TestParam(operationMode = Operation.Mode.IMPOSED)
        )
    }

    @Test
    fun spotlight_calculates_visible_elements_correctly_when_clipToBounds_is_false() {
        createSpotlightSlider(initialActiveIndex = 1f)
        composeTestRule.setupAppyxComponent(spotlight, 0.67f, clipToBounds = false)
        checkTargetsOnScreen(setOf(TestTarget.Child1, TestTarget.Child2, TestTarget.Child3))

        if (testParam.operationMode == Operation.Mode.KEYFRAME) {
            val animationDuration = 1000
            spotlight.last(mode = testParam.operationMode, animationSpec = tween(animationDuration))
            composeTestRule.mainClock.advanceTimeBy(animationDuration.toLong())
        } else {
            spotlight.last(mode = testParam.operationMode)
            spotlight.waitUntilAnimationEnded(composeTestRule)
        }

        composeTestRule.waitForIdle()

        checkTargetsOnScreen(setOf(TestTarget.Child5, TestTarget.Child4))
    }

    @Test
    fun spotlight_calculates_visible_elements_correctly_when_clipToBounds_is_true() {
        createSpotlightSlider(initialActiveIndex = 1f)
        composeTestRule.setupAppyxComponent(spotlight, 0.67f, clipToBounds = true)

        if (testParam.operationMode == Operation.Mode.KEYFRAME) {
            val animationDuration = 1000
            spotlight.last(mode = testParam.operationMode, animationSpec = tween(animationDuration))
            composeTestRule.mainClock.advanceTimeBy(animationDuration.toLong())
        } else {
            spotlight.last(mode = testParam.operationMode)
            spotlight.waitUntilAnimationEnded(composeTestRule)
        }

        composeTestRule.waitForIdle()

        checkTargetsOnScreen(setOf(TestTarget.Child5))
    }

    private fun checkTargetsOnScreen(targets: Set<TestTarget>) {
        assertEquals(
            targets,
            spotlight.elements.value.onScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun checkTargetsOffScreen(targets: Set<TestTarget>) {
        assertEquals(
            targets,
            spotlight.elements.value.offScreen.map { it.interactionTarget }.toSet()
        )
    }

    private fun createSpotlightSlider(
        disableAnimations: Boolean = false,
        initialActiveIndex: Float = 0f,
    ) {
        val model = SpotlightModel(
            items = listOf(
                TestTarget.Child1,
                TestTarget.Child2,
                TestTarget.Child3,
                TestTarget.Child4,
                TestTarget.Child5
            ),
            initialActiveIndex = initialActiveIndex,
            savedStateMap = null
        )
        spotlight = Spotlight(
            model = model,
            visualisation = { SpotlightSlider(uiContext = it, initialState = model.currentState) },
            scope = CoroutineScope(Dispatchers.Unconfined),
            disableAnimations = disableAnimations,
        )
    }
}
