package com.bumble.appyx.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.utils.multiplatform.Parcelize


@Parcelize
class Last<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        fromState.copy(
            activeIndex = fromState.positions.lastIndex.toFloat(),
        )
}

fun <InteractionTarget : Any> SpotlightHero<InteractionTarget>.last(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Last(mode), animationSpec)
}
