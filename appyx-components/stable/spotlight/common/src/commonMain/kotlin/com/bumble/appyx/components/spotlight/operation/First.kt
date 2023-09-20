package com.bumble.appyx.components.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize


@Parcelize
class First<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(
        baseLineState: SpotlightModel.State<InteractionTarget>
    ): SpotlightModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightModel.State<InteractionTarget>
    ): SpotlightModel.State<InteractionTarget> =
        fromState.copy(
            activeIndex = 0f,
        )
}

fun <InteractionTarget : Any> Spotlight<InteractionTarget>.first(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(First(mode), animationSpec)
}
