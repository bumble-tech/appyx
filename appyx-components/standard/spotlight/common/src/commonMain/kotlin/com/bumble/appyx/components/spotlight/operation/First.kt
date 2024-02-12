package com.bumble.appyx.components.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize


@Parcelize
class First<NavTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(
        baseLineState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightModel.State<NavTarget>
    ): SpotlightModel.State<NavTarget> =
        fromState.copy(
            activeIndex = 0f,
        )
}

fun <NavTarget : Any> Spotlight<NavTarget>.first(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(First(mode), animationSpec)
}
