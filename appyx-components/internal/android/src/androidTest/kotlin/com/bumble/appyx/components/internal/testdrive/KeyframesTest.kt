package com.bumble.appyx.components.internal.testdrive

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bumble.appyx.components.internal.testdrive.helper.createTestDrive
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.testing.snapshot
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class KeyframesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun basic_behaviour_one_segment() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_sequential() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        repeat(4) {
            testDrive.operation(
                operation = Next(Operation.Mode.KEYFRAME)
            )

            composeTestRule.mainClock.advanceTimeBy(1000)
        }

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_keyframe_is_running_then_possible_to_enqueue_more_operations() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        repeat(2) {
            testDrive.operation(
                operation = Next(Operation.Mode.KEYFRAME)
            )

            composeTestRule.mainClock.advanceTimeBy(700)
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_all_at_once() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        repeat(4) {
            testDrive.operation(
                operation = Next(Operation.Mode.KEYFRAME)
            )
        }

        composeTestRule.mainClock.advanceTimeBy(1000)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_passing_segment_threshold_then_no_artefacts() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(2000)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @OptIn(
        ExperimentalMaterialApi::class
    )
    @Test
    @Ignore("This test attempts to find flickers and artifacts that were fixed in the past. However the artifacts do not show up when running this test")
    fun validate_that_no_artefacts_appear_during_animation() {
        composeTestRule.setContent {
            TestDriveExperiment()
        }

        composeTestRule.mainClock.autoAdvance = false

        repeat(30) {
            repeat(250) {
                composeTestRule.mainClock.advanceTimeBy(1, true)
            }
            composeTestRule.onNodeWithText("Keyframe").performClick()
        }
    }

}
