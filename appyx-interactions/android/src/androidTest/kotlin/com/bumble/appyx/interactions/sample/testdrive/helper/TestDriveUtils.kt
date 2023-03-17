package com.bumble.appyx.interactions.sample.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveMotionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


fun createTestDrive(
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 1000,
        easing = LinearEasing
    )
) =
    TestDrive(
        scope = CoroutineScope(Dispatchers.Unconfined),
        model = TestDriveModel(NavTarget.Child1),
        motionController = { TestDriveMotionController(it) },
        progressAnimationSpec = animationSpec,
        gestureFactory = { TestDriveMotionController.Gestures(it) },
    )


