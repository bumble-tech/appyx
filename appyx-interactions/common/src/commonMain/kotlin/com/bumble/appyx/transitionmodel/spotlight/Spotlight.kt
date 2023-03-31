package com.bumble.appyx.transitionmodel.spotlight

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

open class Spotlight<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: SpotlightModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, SpotlightModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, SpotlightModel.State<InteractionTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
    disableAnimations: Boolean = false,
    isDebug: Boolean = false
) : BaseInteractionModel<InteractionTarget, SpotlightModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    disableAnimations = disableAnimations,
    isDebug = isDebug
)
