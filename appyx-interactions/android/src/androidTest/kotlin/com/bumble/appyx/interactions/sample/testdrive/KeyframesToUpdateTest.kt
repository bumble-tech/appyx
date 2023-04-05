package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.animation.core.spring
import androidx.compose.ui.test.junit4.createComposeRule
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createTestDrive
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class KeyframesToUpdateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    //    @Ignore("Fix me, this test is flaky on CI")
    @Test
    fun when_in_segment_interrupt_with_update() {
        val animationSpec = spring<Float>()
        val testDrive = composeTestRule.createTestDrive(animationSpec = animationSpec)
        composeTestRule.mainClock.autoAdvance = false

        testDrive.operation(
            operation = Next(Operation.Mode.KEYFRAME),
            animationSpec = animationSpec
        )

        repeat(3) {
            composeTestRule.mainClock.advanceTimeByFrame()
        }

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
