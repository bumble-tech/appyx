package com.bumble.appyx.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Activate<InteractionTarget : Any>(
    private val index: Float,
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<InteractionTarget>): Boolean =
        index != state.activeIndex &&
                (index in 0f..state.positions.lastIndex.toFloat())

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        fromState.copy(
            activeIndex = index,
        )
}

fun <InteractionTarget : Any> SpotlightHero<InteractionTarget>.activate(
    index: Float,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Activate(index, mode), animationSpec)
}
