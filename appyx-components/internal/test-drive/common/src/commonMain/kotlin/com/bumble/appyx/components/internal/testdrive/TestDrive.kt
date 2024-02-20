package com.bumble.appyx.components.internal.testdrive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class TestDrive<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TestDriveModel<InteractionTarget>,
    visualisation: (UiContext) -> Visualisation<InteractionTarget, TestDriveModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    progressAnimationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(),
    animateSettle: Boolean = false
) : BaseAppyxComponent<InteractionTarget, TestDriveModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    gestureSettleConfig = gestureSettleConfig,
    defaultAnimationSpec = progressAnimationSpec,
    animateSettle = animateSettle
)
