package com.bumble.appyx.transitionmodel.testdrive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class TestDrive<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TestDriveModel<NavTarget>,
    interpolator: (TransitionBounds) -> Interpolator<NavTarget, TestDriveModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, TestDriveModel.State<NavTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
) : InteractionModel<NavTarget, TestDriveModel.State<NavTarget>>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec
)
