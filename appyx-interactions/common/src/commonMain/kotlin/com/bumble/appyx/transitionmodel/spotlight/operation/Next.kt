package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel

@Parcelize
class Next<NavTarget : Any> : Operation<SpotlightModel.State<NavTarget>> {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        state.hasNext()

    override fun invoke(baselineState: SpotlightModel.State<NavTarget>): NavTransition<SpotlightModel.State<NavTarget>> {
        val fromState = baselineState
        val targetState = fromState.copy(
            activeIndex = fromState.activeIndex + 1f,
        )

        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> Spotlight<NavTarget>.next(animationSpec: AnimationSpec<Float> = defaultAnimationSpec) {
    operation(Next<NavTarget>(), animationSpec)
}
