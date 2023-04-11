package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTouchInput
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.sample.SPOTLIGHT_EXPERIMENT_TEST_HELPER
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createSpotlight
import com.bumble.appyx.transitionmodel.spotlight.operation.next
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class GeometryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun when_keyframe_operation_then_scroll_using_geometry() {
        val spotlight = composeTestRule.createSpotlight(
            items = listOf(Child1, Child2, Child3)
        )
        composeTestRule.mainClock.autoAdvance = false

        spotlight.next(
            mode = Operation.Mode.KEYFRAME
        )

        composeTestRule.mainClock.advanceTimeBy(200)

        spotlight.next()

        composeTestRule.mainClock.advanceTimeBy(200)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_update__operation_then_scroll_using_geometry() {
        val spotlight = composeTestRule.createSpotlight(
            items = listOf(Child1, Child2, Child3)
        )
        composeTestRule.mainClock.autoAdvance = false

        spotlight.next(
            mode = Operation.Mode.IMMEDIATE
        )

        composeTestRule.mainClock.advanceTimeBy(200)

        spotlight.next()

        composeTestRule.mainClock.advanceTimeBy(200)

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    @Ignore("This test fails because DragProcessController can't receive 0 or negative drag")
    fun perform_gesture_slide() {
        val spotlight = composeTestRule.createSpotlight(
            items = listOf(Child1, Child2, Child3, Child4, Child5, Child6)
        )
        val first = spotlight.uiModels.value.first()

        val child = composeTestRule.onNode(
            hasContentDescription("${SPOTLIGHT_EXPERIMENT_TEST_HELPER}_${first.element.id}")
        )

        child
            .performTouchInput {
                down(Offset.Zero)
            }
            .performTouchInput {
                moveBy(Offset(-200f, 0f), 500L)
            }
            .performTouchInput {
                composeTestRule.snapshot("${this@GeometryTest.javaClass.simpleName}_${nameRule.methodName}")
                up()
            }
    }
}
