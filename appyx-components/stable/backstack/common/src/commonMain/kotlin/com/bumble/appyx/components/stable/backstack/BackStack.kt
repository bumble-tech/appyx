package com.bumble.appyx.components.stable.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.components.stable.backstack.backpresshandler.PopBackstackStrategy
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackStack<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, BackStackModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, BackStackModel.State<InteractionTarget>> = { GestureFactory.Noop() },
    backPressStrategy: BackPressHandlerStrategy<InteractionTarget, BackStackModel.State<InteractionTarget>> = PopBackstackStrategy(scope),
    animationSpec: AnimationSpec<Float> = spring(),
    disableAnimations: Boolean = false,
    isDebug: Boolean = false
) : BaseInteractionModel<InteractionTarget, BackStackModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    backPressStrategy = backPressStrategy,
    defaultAnimationSpec = animationSpec,
    disableAnimations = disableAnimations,
    isDebug = isDebug
)

