package com.bumble.appyx.components.testdrive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class TestDrive<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TestDriveModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> = { GestureFactory.Noop() },
    progressAnimationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false
) : BaseInteractionModel<InteractionTarget, TestDriveModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = progressAnimationSpec,
    animateSettle = animateSettle
)
