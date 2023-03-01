package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.transitionmodel.backstack.backpresshandler.PopBackstackStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackStack<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<NavTarget>,
    interpolator: (UiContext) -> Interpolator<NavTarget, BackStackModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, BackStackModel.State<NavTarget>> = { GestureFactory.Noop() },
    backPressStrategy: BackPressHandlerStrategy<NavTarget, BackStackModel.State<NavTarget>> = PopBackstackStrategy(),
    animationSpec: AnimationSpec<Float> = spring(),
    isDebug: Boolean = false
) : InteractionModel<NavTarget, BackStackModel.State<NavTarget>>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    backPressStrategy = backPressStrategy,
    defaultAnimationSpec = animationSpec,
    isDebug = isDebug
)

