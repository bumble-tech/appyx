package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createTestDrive
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class UpdateToKeyframesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun when_update_not_settled_keyframe_is_update() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        composeTestRule.mainClock.advanceTimeBy(50)

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(50)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_animation_settles_go_back_to_keyframe_mode() {
        val testDrive = composeTestRule.createTestDrive()
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        composeTestRule.mainClock.advanceTimeBy(1000)

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

}
