package com.bumble.appyx.transitionmodel.spotlight

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class Spotlight<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: SpotlightModel<NavTarget>,
    interpolator: (UiContext) -> Interpolator<NavTarget, SpotlightModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, SpotlightModel.State<NavTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
    isDebug: Boolean = false
) : InteractionModel<NavTarget, SpotlightModel.State<NavTarget>>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    isDebug = isDebug
)
