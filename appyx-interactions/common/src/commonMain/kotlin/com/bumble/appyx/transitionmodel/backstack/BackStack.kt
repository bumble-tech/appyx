package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.transitionmodel.backstack.operation.Pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BackStack<NavTarget : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    val model: BackStackModel<NavTarget>,
    interpolator: (UiContext) -> Interpolator<NavTarget, BackStackModel.State<NavTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, BackStackModel.State<NavTarget>> = { GestureFactory.Noop() },
    animationSpec: AnimationSpec<Float> = spring(),
    isDebug: Boolean = false
) : InteractionModel<NavTarget, BackStackModel.State<NavTarget>>(
    scope = scope,
    model = model,
    interpolator = interpolator,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    isDebug = isDebug
) {
    override fun handleBackNavigation(): Boolean {
        val pop = Pop<NavTarget>()
        //todo find a better way to check if operation is applicable
        return if (pop.isApplicable(model.output.value.currentTargetState)) {
            operation(Pop())
            true
        } else false
    }

    override fun canHandeBackNavigation(): Flow<Boolean> =
        model.output.map { it.currentTargetState }.map { it.stashed.isNotEmpty() }
}
