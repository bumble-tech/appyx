package com.bumble.appyx.components.internal.testdrive.android

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.internal.testdrive.TEST_DRIVE_EXPERIMENT_TEST_HELPER
import com.bumble.appyx.components.internal.testdrive.android.helper.createTestDrive
import com.bumble.appyx.interactions.testing.snapshot
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
        composeTestRule.createTestDrive()

        val square =
            composeTestRule.onNode(hasContentDescription(TEST_DRIVE_EXPERIMENT_TEST_HELPER))

        square.performTouchInput {
            down(Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 100.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}")
        }
    }

    @Test
    fun when_drag_past_maximum_value_then_second_at_maximum_value_is_visible() {
        composeTestRule.createTestDrive()

        val square =
            composeTestRule.onNode(hasContentDescription(TEST_DRIVE_EXPERIMENT_TEST_HELPER))

        square.performTouchInput {
            down(Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 1000.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            up()
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}")
        }
    }

    @Test
    fun when_released_before_threshold_snaps_to_origin() {
        composeTestRule.createTestDrive()

        val square =
            composeTestRule.onNode(hasContentDescription(TEST_DRIVE_EXPERIMENT_TEST_HELPER))

        square.performTouchInput {
            down(Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 50.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            up()
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}")
        }
    }

    @Test
    fun when_released_after_threshold_snaps_to_destination() {
        composeTestRule.createTestDrive()

        val square =
            composeTestRule.onNode(hasContentDescription(TEST_DRIVE_EXPERIMENT_TEST_HELPER))

        square.performTouchInput {
            down(Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 101.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            up()
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}")
        }
    }

    @Test
    fun given_element_dragged_to_the_second_position_when_dragged_back_then_first_element_is_visible() {
        composeTestRule.createTestDrive()

        val square =
            composeTestRule.onNode(hasContentDescription(TEST_DRIVE_EXPERIMENT_TEST_HELPER))
        square.performTouchInput {
            down(Offset.Zero)
        }.performTouchInput {
            with(composeTestRule.density) {
                val x = 200.dp.toPx()
                moveBy(Offset(x, 0f), 500L)
            }
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}_1")
            with(composeTestRule.density) {
                val x = 200.dp.toPx()
                moveBy(Offset(-x, 0f), 500L)
            }
        }.performTouchInput {
            composeTestRule.snapshot("${this@GesturesTest.javaClass.simpleName}_${nameRule.methodName}_2")
            up()
        }
    }
}
