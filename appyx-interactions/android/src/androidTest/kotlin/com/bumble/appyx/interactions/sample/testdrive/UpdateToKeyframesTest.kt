package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.TestDriveController
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
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        testController.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        testController.advanceTimeBy(50)

        testController.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        testController.advanceTimeBy(50)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_animation_settles_go_back_to_keyframe_mode() {
        val testController = TestDriveController(
            composeTestRule = composeTestRule,
            autoAdvance = false
        )

        testController.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        testController.advanceTimeBy(1000)

        testController.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        testController.advanceTimeBy(500)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

}
