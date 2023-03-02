package com.bumble.appyx.transitionmodel.cards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext

open class Cards<NavTarget : Any>(
    model: CardsModel<NavTarget>,
    motionController: (UiContext) -> MotionController<NavTarget, CardsModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, CardsModel.State<NavTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false,
) : InteractionModel<NavTarget, CardsModel.State<NavTarget>>(
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    animateSettle = animateSettle
)
