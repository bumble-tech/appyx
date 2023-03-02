package com.bumble.appyx.transitionmodel.testdrive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class TestDrive<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TestDriveModel<NavTarget>,
    motionController: (UiContext) -> MotionController<NavTarget, TestDriveModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, TestDriveModel.State<NavTarget>> = { GestureFactory.Noop() },
    progressAnimationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false
) : InteractionModel<NavTarget, TestDriveModel.State<NavTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = progressAnimationSpec,
    animateSettle = animateSettle
)
