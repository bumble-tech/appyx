package com.bumble.appyx.components.testdrive

import androidx.compose.animation.core.spring
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.components.testdrive.helper.createTestDrive
import com.bumble.appyx.components.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.testing.snapshot
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class KeyframesToUpdateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun when_in_segment_interrupt_with_update() {
        val animationSpec = spring<Float>()
        val testDrive = composeTestRule.createTestDrive(animationSpec = animationSpec)
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME),
            animationSpec = animationSpec
        )

        composeTestRule.mainClock.advanceTimeByFrame()

        testDrive.operation(
            operation = Next(Operation.Mode.IMMEDIATE),
            animationSpec = animationSpec
        )

        repeat(3) {
            composeTestRule.mainClock.advanceTimeByFrame()
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
