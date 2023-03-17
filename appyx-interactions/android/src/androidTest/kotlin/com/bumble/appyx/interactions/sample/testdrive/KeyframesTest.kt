package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createTestDrive
import com.bumble.appyx.interactions.setupTestDrive
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
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_sequential() {
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

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
    fun basic_behaviour_all_at_once() {
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

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
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(2000)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
