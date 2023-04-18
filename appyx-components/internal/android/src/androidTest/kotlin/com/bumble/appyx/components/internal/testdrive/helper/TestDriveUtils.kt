package com.bumble.appyx.components.internal.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.components.internal.testdrive.TestDrive
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveUi
import com.bumble.appyx.components.internal.testdrive.ui.TestDriveMotionController
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.theme.appyx_dark
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
        gestureFactory = {
            TestDriveMotionController.Gestures(
                it
            )
        },
    ).also { setupTestDrive(it, model) }
}

@OptIn(ExperimentalMaterialApi::class)
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
                testDrive = testDrive,
                model = testDriveModel
            )
        }
    }
}


