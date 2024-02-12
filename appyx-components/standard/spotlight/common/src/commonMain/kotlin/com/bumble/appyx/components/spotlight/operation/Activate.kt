package com.bumble.appyx.components.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Activate<NavTarget : Any>(
    private val index: Float,
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        index != state.activeIndex &&
                (index in 0f..state.positions.lastIndex.toFloat())

    override fun createFromState(
        baseLineState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> =
        fromState.copy(
            activeIndex = index,
        )
}

fun <NavTarget : Any> Spotlight<NavTarget>.activate(
    index: Float,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Activate(index, mode), animationSpec)
}
