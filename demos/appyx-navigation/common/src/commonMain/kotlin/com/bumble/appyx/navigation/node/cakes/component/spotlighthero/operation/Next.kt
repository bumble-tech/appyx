package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHero
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Next<InteractionTarget>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<InteractionTarget>): Boolean =
        state.hasNext()

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<InteractionTarget>
    ): SpotlightHeroModel.State<InteractionTarget> =
        fromState.copy(
            activeIndex = fromState.activeIndex + 1f
        )
}

fun <InteractionTarget : Any> SpotlightHero<InteractionTarget>.next(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Next(mode), animationSpec)
}
