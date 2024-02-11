package com.bumble.appyx.components.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.components.backstack.backpresshandler.PopBackstackStrategy
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackStack<InteractionTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<InteractionTarget>,
    visualisation: (UiContext) -> Visualisation<InteractionTarget, BackStackModel.State<InteractionTarget>>,
    animationSpec: AnimationSpec<Float> = spring(),
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, BackStackModel.State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(),
    backPressStrategy: BackPressHandlerStrategy<InteractionTarget, BackStackModel.State<InteractionTarget>> =
        PopBackstackStrategy(scope),
    disableAnimations: Boolean = false,
) : BaseAppyxComponent<InteractionTarget, BackStackModel.State<InteractionTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    gestureSettleConfig = gestureSettleConfig,
    backPressStrategy = backPressStrategy,
    defaultAnimationSpec = animationSpec,
    disableAnimations = disableAnimations
)

