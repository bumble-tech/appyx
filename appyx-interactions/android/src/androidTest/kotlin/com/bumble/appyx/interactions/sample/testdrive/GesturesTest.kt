package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.createTestDrive
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class GesturesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun drag_to_middle() {
        val testDrive = composeTestRule.createTestDrive()

        with(composeTestRule.density) {
            val x = 100.dp.toPx()
            testDrive.onDrag(Offset(x, 0f), this)
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_drag_past_maximum_value_then_second_at_maximum_value_is_visible() {
        val testDrive = composeTestRule.createTestDrive()

        with(composeTestRule.density) {
            val x = 1000.dp.toPx()
            testDrive.onDrag(Offset(x, 0f), this)
            testDrive.onDragEnd()
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_released_before_threshold_snaps_to_origin() {
        val testDrive = composeTestRule.createTestDrive()

        with(composeTestRule.density) {
            val x = 50.dp.toPx()
            testDrive.onDrag(Offset(x, 0f), this)
            testDrive.onDragEnd()
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_released_after_threshold_snaps_to_destination() {
        val testDrive = composeTestRule.createTestDrive()

        with(composeTestRule.density) {
            val x = 100.dp.toPx()
            testDrive.onDrag(Offset(x, 0f), this)
            testDrive.onDragEnd()
        }

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun given_element_dragged_to_the_second_position_when_dragged_back_then_first_element_is_visible() {
        composeTestRule.createTestDrive()

        val square = composeTestRule.onNode(hasContentDescription("TheSquare"))
        square.performTouchInput {
            down(0, Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 200.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}_1")
            with(composeTestRule.density) {
                val x = 200.dp.toPx()
                moveBy(Offset(-x, 0f), 500L)
            }
        }.performTouchInput {
            composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}_2")
            up()
        }
    }
}
