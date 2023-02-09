package com.bumble.appyx.interactions.ui.checks

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.ui.InteractionModelSetup
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.sample.TestDriveUi
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class TestDriveExperimentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun testDrive_KeyFrame_After_500() = runTest {
        composeTestRule.mainClock.autoAdvance = false

        val testAnimationSpec = tween<Float>(durationMillis = 1000, easing = LinearEasing)

        var testDrive: TestDrive<NavTarget>? = null

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
                        interpolator = {
                            TestDriveUiModel(coroutineScope = coroutineScope)
                        },
                        gestureFactory = { TestDriveUiModel.Gestures(it) },
                    )
                }

                InteractionModelSetup(testDrive!!)

                TestDriveUi(
                    testDrive = testDrive!!,
                    model = model
                )
            }
        }

        testDrive!!.operation(
            operation = Next(KEYFRAME),
            animationSpec = testAnimationSpec
        )

        composeTestRule.mainClock.advanceTimeBy(500)

        takeScreenshot()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
