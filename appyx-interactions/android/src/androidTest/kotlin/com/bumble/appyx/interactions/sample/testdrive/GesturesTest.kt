package com.bumble.appyx.interactions.sample.testdrive

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.snapshot
import com.bumble.appyx.interactions.sample.testdrive.helper.TestDriveController
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
        val testController = TestDriveController(composeTestRule)

        val x = with(testController.density!!) { 100.dp.toPx() }
        testController.onDrag(Offset(x, 0f))

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun drag_past_maximum_value() {
        val testController = TestDriveController(composeTestRule)

        val x = with(testController.density!!) { 1000.dp.toPx() }
        testController.onDrag(Offset(x, 0f))
        testController.onDragEnd()

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_released_before_threshold_snaps_to_origin() {
        val testController = TestDriveController(composeTestRule)

        val x = with(testController.density!!) { 50.dp.toPx() }
        testController.onDrag(Offset(x, 0f))
        testController.onDragEnd()

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun when_released_after_threshold_snaps_to_destination() {
        val testController = TestDriveController(composeTestRule)

        val x = with(testController.density!!) { 100.dp.toPx() }
        testController.onDrag(Offset(x, 0f))
        testController.onDragEnd()

        composeTestRule.snapshot("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
