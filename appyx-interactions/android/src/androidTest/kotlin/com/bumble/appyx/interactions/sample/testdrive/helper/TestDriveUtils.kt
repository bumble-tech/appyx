package com.bumble.appyx.interactions.sample.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.setupTestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveMotionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


fun ComposeContentTestRule.createTestDrive(
    animationSpec: AnimationSpec<Float>? = tween(
        durationMillis = 1000,
        easing = LinearEasing
    ),
    uiAnimationSpec: SpringSpec<Float> = spring()
): TestDrive<NavTarget> {
    val model = TestDriveModel(
        element = NavTarget.Child1,
        savedStateMap = null
    )
    return TestDrive(
        scope = CoroutineScope(Dispatchers.Unconfined),
        model = model,
        motionController = {
            TestDriveMotionController(
                uiContext = it,
                uiAnimationSpec = uiAnimationSpec
            )
        },
        progressAnimationSpec = animationSpec ?: spring(),
        gestureFactory = { TestDriveMotionController.Gestures(it) },
    ).also { setupTestDrive(it, model) }
}



