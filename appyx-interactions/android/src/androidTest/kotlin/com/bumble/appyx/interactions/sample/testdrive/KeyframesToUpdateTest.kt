package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.TestDriveController
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class KeyframesToUpdateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    // TODO: Fix me, this causes random failures
//    @Test
//    fun when_in_segment_interrupt_with_update() {
//        val testController = TestDriveController(
//            composeTestRule = composeTestRule,
//            autoAdvance = false,
//            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
//        )
//
//        testController.operation(
//            operation = Next(Operation.Mode.KEYFRAME)
//        )
//
//        testController.advanceTimeBy(100)
//
//        testController.operation(
//            operation = Next(Operation.Mode.IMMEDIATE)
//        )
//
//        testController.advanceTimeBy(100)
//
//        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
//    }
}
