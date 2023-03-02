package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.transitionmodel.backstack.backpresshandler.PopBackstackStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackStack<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, BackStackModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, BackStackModel.State<InteractionTarget>> = { GestureFactory.Noop() },
    backPressStrategy: BackPressHandlerStrategy<InteractionTarget, BackStackModel.State<InteractionTarget>> = PopBackstackStrategy(),
    animationSpec: AnimationSpec<Float> = spring(),
    isDebug: Boolean = false
) : InteractionModel<InteractionTarget, BackStackModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    backPressStrategy = backPressStrategy,
    defaultAnimationSpec = animationSpec,
    isDebug = isDebug
)

