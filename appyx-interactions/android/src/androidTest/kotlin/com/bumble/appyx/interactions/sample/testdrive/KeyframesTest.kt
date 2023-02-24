package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.TestDriveController
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
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
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        testController.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        testController.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_sequential() {
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        for (i in 1..4) {
            testController.operation(
                operation = Next(Operation.Mode.KEYFRAME)
            )

            testController.advanceTimeBy(1000)
        }

        testController.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        testController.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_all_at_once() {
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        for (i in 1..4) {
            testController.operation(
                operation = Next(Operation.Mode.KEYFRAME)
            )
        }

        testController.advanceTimeBy(1000)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_passing_segment_threshold_then_no_artefacts() {
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        testController.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        testController.advanceTimeBy(2000)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
