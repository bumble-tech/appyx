package com.bumble.appyx.transitionmodel.promoter

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class Promoter<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: PromoterModel<NavTarget>,
    interpolator: (TransitionBounds) -> Interpolator<NavTarget, PromoterModel.State>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, PromoterModel.State> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
) : InteractionModel<NavTarget, PromoterModel.State>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec
)
