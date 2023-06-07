package com.bumble.appyx.components.internal.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.components.internal.testdrive.TestDrive
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveUi
import com.bumble.appyx.components.internal.testdrive.ui.simple.TestDriveMotionController
import com.bumble.appyx.interactions.core.gesture.permissiveValidator
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.InteractionTarget
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt


fun ComposeContentTestRule.createTestDrive(
    animationSpec: AnimationSpec<Float>? = tween(
        durationMillis = 1000,
        easing = LinearEasing
    ),
    uiAnimationSpec: SpringSpec<Float> = spring()
): TestDrive<InteractionTarget> {
    val model = TestDriveModel(
        element = InteractionTarget.Child1,
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
        gestureFactory = {
            TestDriveMotionController.Gestures(
                it
            )
        },
    ).also { setupTestDrive(it, model) }
}

fun <InteractionTarget : Any> ComposeContentTestRule.setupTestDrive(
    testDrive: TestDrive<InteractionTarget>,
    testDriveModel: TestDriveModel<InteractionTarget>,
) {
    setContent {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = appyx_dark
        ) {
            InteractionModelSetup(testDrive)

            TestDriveUi(
                screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
                screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
                testDrive = testDrive,
                model = testDriveModel,
                gestureValidator = permissiveValidator,
            )
        }
    }
}


