package com.bumble.appyx.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class ToggleHeroMode<InteractionTarget>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {

    override fun isApplicable(state: State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(
            mode = when (fromState.mode) {
                LIST -> HERO
                HERO -> LIST
            }
        )
}

fun <InteractionTarget : Any> SpotlightHero<InteractionTarget>.toggleHeroMode(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(ToggleHeroMode(mode), animationSpec)
}
