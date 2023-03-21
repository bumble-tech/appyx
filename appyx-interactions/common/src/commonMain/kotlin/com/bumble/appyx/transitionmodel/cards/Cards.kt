package com.bumble.appyx.transitionmodel.cards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext

open class Cards<InteractionTarget : Any>(
    model: CardsModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, CardsModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, CardsModel.State<InteractionTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false,
) : BaseInteractionModel<InteractionTarget, CardsModel.State<InteractionTarget>>(
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    animateSettle = animateSettle
)
