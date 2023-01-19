package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel


@Parcelize
class First<NavTarget : Any> : Operation<SpotlightModel.State<NavTarget>> {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        true

    override fun invoke(baselineState: SpotlightModel.State<NavTarget>): NavTransition<SpotlightModel.State<NavTarget>> {
        val fromState = baselineState
        val targetState = fromState.copy(
            activeIndex = 0f,
        )

        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> Spotlight<NavTarget>.first(animationSpec: AnimationSpec<Float> = defaultAnimationSpec) {
    operation(First<NavTarget>(), animationSpec)
}
