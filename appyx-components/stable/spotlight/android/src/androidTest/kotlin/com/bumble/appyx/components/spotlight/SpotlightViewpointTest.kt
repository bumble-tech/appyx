package com.bumble.appyx.components.spotlight

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTouchInput
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.utils.SPOTLIGHT_EXPERIMENT_TEST_HELPER
import com.bumble.appyx.components.spotlight.utils.createSpotlight
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.InteractionTarget.Child1
import com.bumble.appyx.interactions.sample.InteractionTarget.Child2
import com.bumble.appyx.interactions.sample.InteractionTarget.Child3
import com.bumble.appyx.interactions.sample.InteractionTarget.Child4
import com.bumble.appyx.interactions.sample.InteractionTarget.Child5
import com.bumble.appyx.interactions.sample.InteractionTarget.Child6
import com.bumble.appyx.interactions.testing.snapshot
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class SpotlightViewpointTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun when_keyframe_operation_then_scroll_using_viewpoint() {
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
    fun when_update__operation_then_scroll_using_viewpoint() {
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
                composeTestRule.snapshot("${this@SpotlightViewpointTest.javaClass.simpleName}_${nameRule.methodName}")
                up()
            }
    }
}
