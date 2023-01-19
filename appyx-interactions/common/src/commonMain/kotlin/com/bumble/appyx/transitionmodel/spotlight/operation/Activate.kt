package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel

@Parcelize
class Activate<NavTarget : Any>(
    private val index: Float
) : BaseOperation<SpotlightModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        index != state.activeIndex && 0 <= index && index <= state.standard.lastIndex

    override fun createFromState(baseLineState: SpotlightModel.State<NavTarget>): SpotlightModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(fromState: SpotlightModel.State<NavTarget>): SpotlightModel.State<NavTarget> =
        fromState.copy(
            activeIndex = fromState.standard.lastIndex.toFloat(),
        )
}

fun <NavTarget : Any> Spotlight<NavTarget>.activate(index: Float, animationSpec: AnimationSpec<Float> = defaultAnimationSpec) {
    operation(Activate<NavTarget>(index), animationSpec)
}
