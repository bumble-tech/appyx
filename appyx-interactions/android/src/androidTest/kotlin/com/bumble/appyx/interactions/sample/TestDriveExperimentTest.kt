package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel.Companion.toProps
import junit.framework.Assert.assertEquals
import kotlin.math.roundToInt
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class TestDriveExperimentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterialApi::class)
    @Test
    fun myTest() {
        composeTestRule.mainClock.autoAdvance = false

        val testAnimationSpec = tween<Float>(durationMillis = 1000, easing = LinearEasing)

        val model = TestDriveModel(NavTarget.Child1)

        composeTestRule.setContent {
            TestDriveExperiment(
                animationSpec = testAnimationSpec,
                transitionModel = model
            )
        }

        composeTestRule.onNodeWithText("Keyframe").performClick()

        composeTestRule.mainClock.advanceTimeBy(500)

        runBlocking {
            val expectedProps = TestDriveUiModel.Props()
            expectedProps.lerpTo(TestDriveUiModel.a, TestDriveUiModel.b, 0.5f)

            val output = model.output.value
            if (output is Keyframes<TestDriveModel.State<NavTarget>>) {
                val progress = (output.progress * 10).roundToInt().toFloat() / 10
                val fromState = output.currentSegment.fromState
                val targetState = output.currentSegment.targetState

                val actualProps = TestDriveUiModel.Props()
                val startProps = fromState.elementState.toProps()
                val endProps = targetState.elementState.toProps()

                actualProps.lerpTo(startProps, endProps, progress)

                assertEquals(expectedProps.offset.value.x, actualProps.offset.value.x)
                assertEquals(expectedProps.offset.value.y, actualProps.offset.value.y)
                assertEquals(
                    expectedProps.backgroundColor.value.value,
                    actualProps.backgroundColor.value.value
                )
            }
        }
    }
}
