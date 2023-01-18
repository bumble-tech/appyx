package com.bumble.appyx.transitionmodel.spotlight

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class Spotlight<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: SpotlightModel<NavTarget>,
    propsMapper: Interpolator<NavTarget, SpotlightModel.State>,
    gestureFactory: GestureFactory<NavTarget, SpotlightModel.State> = GestureFactory.Noop(),
    animationSpec: AnimationSpec<Float> = spring(),
) : InteractionModel<NavTarget, SpotlightModel.State>(
    scope = scope,
    model = model,
    interpolator = propsMapper,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec
)
