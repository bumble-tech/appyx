package com.bumble.appyx.transitionmodel.cards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class Cards<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: CardsModel<NavTarget>,
    interpolator: (TransitionBounds) -> Interpolator<NavTarget, CardsModel.State>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, CardsModel.State> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring()
) : InteractionModel<NavTarget, CardsModel.State>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
)
