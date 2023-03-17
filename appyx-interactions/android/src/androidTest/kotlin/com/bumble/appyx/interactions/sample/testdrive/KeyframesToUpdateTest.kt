package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createTestDrive
import com.bumble.appyx.interactions.setupInteractionModel
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class KeyframesToUpdateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Ignore("Fix me, this test is flaky on CI")
    @Test
    fun when_in_segment_interrupt_with_update() {
        val testDrive = createTestDrive(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ).also {
            composeTestRule.setupInteractionModel(it)
            composeTestRule.mainClock.autoAdvance = false
        }

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME)
        )

        composeTestRule.mainClock.advanceTimeBy(100)

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE)
        )

        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
