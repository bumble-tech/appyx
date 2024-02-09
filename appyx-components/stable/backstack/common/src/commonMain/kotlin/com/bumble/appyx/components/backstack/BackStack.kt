package com.bumble.appyx.components.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.components.backstack.backpresshandler.PopBackstackStrategy
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackStack<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<NavTarget>,
    visualisation: (UiContext) -> Visualisation<NavTarget, BackStackModel.State<NavTarget>>,
    animationSpec: AnimationSpec<Float> = spring(),
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, BackStackModel.State<NavTarget>> = {
        GestureFactory.Noop()
    },
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(),
    backPressStrategy: BackPressHandlerStrategy<NavTarget, BackStackModel.State<NavTarget>> =
        PopBackstackStrategy(scope),
    disableAnimations: Boolean = false,
) : BaseAppyxComponent<NavTarget, BackStackModel.State<NavTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    gestureSettleConfig = gestureSettleConfig,
    backPressStrategy = backPressStrategy,
    defaultAnimationSpec = animationSpec,
    disableAnimations = disableAnimations
)

