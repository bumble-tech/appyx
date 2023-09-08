package com.bumble.appyx.components.experimental.cards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig

open class Cards<InteractionTarget : Any>(
    model: CardsModel<InteractionTarget>,
    motionController: (UiContext) -> MotionController<InteractionTarget, CardsModel.State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, CardsModel.State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    animationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(
        completionThreshold = 0.15f,
        completeGestureSpec = animationSpec,
        revertGestureSpec = animationSpec,
    ),
    animateSettle: Boolean = false,
) : BaseAppyxComponent<InteractionTarget, CardsModel.State<InteractionTarget>>(
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    gestureSettleConfig = gestureSettleConfig,
    animateSettle = animateSettle
)
