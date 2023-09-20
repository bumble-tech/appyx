package com.bumble.appyx.components.spotlight

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class Spotlight<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: SpotlightModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, SpotlightModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, SpotlightModel.State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    animationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(
        completionThreshold = 0.2f,
        completeGestureSpec = animationSpec,
        revertGestureSpec = animationSpec,
    ),
    disableAnimations: Boolean = false,
    isDebug: Boolean = false
) : BaseAppyxComponent<InteractionTarget, SpotlightModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    gestureSettleConfig = gestureSettleConfig,
    disableAnimations = disableAnimations,
    isDebug = isDebug
)
