package com.bumble.appyx.interactions.sample.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.ui.InteractionModelSetup
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.sample.TestDriveUi
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel


@OptIn(ExperimentalMaterialApi::class)
class TestDriveController(
    private val composeTestRule: ComposeContentTestRule,
    private val animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000, easing = LinearEasing),
    private val autoAdvance: Boolean = true
) {
    private var testDrive: TestDrive<NavTarget>? = null
    var density: Density? = null

    init {
        composeTestRule.mainClock.autoAdvance = autoAdvance

        composeTestRule.setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = appyx_dark
            ) {
                val coroutineScope = rememberCoroutineScope()
                val model = remember { TestDriveModel(NavTarget.Child1) }

                testDrive = remember {
                    TestDrive(
                        scope = coroutineScope,
                        model = model,
                        interpolator = { TestDriveUiModel(it) },
                        progressAnimationSpec = animationSpec,
                        gestureFactory = { TestDriveUiModel.Gestures(it) },
                    )
                }

                density = LocalDensity.current

                InteractionModelSetup(testDrive!!)

                TestDriveUi(
                    testDrive = testDrive!!,
                    model = model
                )
            }
        }

        if (!autoAdvance) {
            composeTestRule.mainClock.advanceTimeByFrame()
        }
    }

    fun advanceTimeBy(milliseconds: Long) {
        if (!autoAdvance) {
            composeTestRule.mainClock.advanceTimeBy(milliseconds)
        }
    }

    fun advanceTimeByFrame() {
        if (!autoAdvance) {
            composeTestRule.mainClock.advanceTimeByFrame()
        }
    }

    fun operation(
        operation: Operation<TestDriveModel.State<NavTarget>>,
    ) {
        testDrive!!.operation(operation, animationSpec)
    }

    fun onDrag(dragAmount: Offset) {
        testDrive!!.onDrag(dragAmount, density!!)
    }

    fun onDragEnd() {
        testDrive!!.onDragEnd()
    }

}
