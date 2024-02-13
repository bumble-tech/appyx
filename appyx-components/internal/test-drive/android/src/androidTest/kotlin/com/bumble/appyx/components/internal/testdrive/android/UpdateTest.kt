package com.bumble.appyx.components.internal.testdrive.android

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.internal.testdrive.android.helper.createTestDrive
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.interactions.utils.testing.snapshot
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
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun basic_behaviour_multiple() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        repeat(3) {
            testDrive.operation(
                operation = Next(Operation.Mode.IMMEDIATE)
            )
            composeTestRule.mainClock.advanceTimeBy(50)
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
