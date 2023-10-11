package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class SetHeroMode<InteractionTarget>(
    private val heroMode: SpotlightHeroModel.Mode,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {

    override fun isApplicable(state: State<InteractionTarget>): Boolean =
        state.mode != heroMode

    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(
            mode = heroMode
        )
}

fun <InteractionTarget : Any> SpotlightHero<InteractionTarget>.setHeroMode(
    heroMode: SpotlightHeroModel.Mode,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(SetHeroMode(heroMode, mode), animationSpec)
}
