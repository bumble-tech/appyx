package com.bumble.appyx.components.spotlight

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

open class Spotlight<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: SpotlightModel<NavTarget>,
    visualisation: (UiContext) -> Visualisation<NavTarget, SpotlightModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, SpotlightModel.State<NavTarget>> = {
        GestureFactory.Noop()
    },
    animationSpec: AnimationSpec<Float> = spring(),
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(
        completionThreshold = 0.2f,
        completeGestureSpec = animationSpec,
        revertGestureSpec = animationSpec,
    ),
    disableAnimations: Boolean = false,
) : BaseAppyxComponent<NavTarget, SpotlightModel.State<NavTarget>>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    gestureSettleConfig = gestureSettleConfig,
    disableAnimations = disableAnimations
) {
    val activeIndex: StateFlow<Float> = model.output
        .mapState(scope) { it.currentTargetState.activeIndex }

    val activeElement: StateFlow<NavTarget?> = model.output
        .mapState(scope) { it.currentTargetState.activeElement }
}
