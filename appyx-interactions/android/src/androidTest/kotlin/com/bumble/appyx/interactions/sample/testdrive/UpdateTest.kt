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

class UpdateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun basic_behaviour_one_update() {
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_multiple() {
        val testDrive = createTestDrive { testDrive, model ->
            composeTestRule.setupTestDrive(testDrive, model)
            composeTestRule.mainClock.autoAdvance = false
        }

        repeat(3) {
            testDrive.operation(
                operation = Next(Operation.Mode.IMMEDIATE)
            )
            composeTestRule.mainClock.advanceTimeBy(50)
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
