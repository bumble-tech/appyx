package com.bumble.appyx.demos.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class SetHeroMode<NavTarget>(
    private val heroMode: SpotlightHeroModel.Mode,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<NavTarget>>() {

    override fun isApplicable(state: State<NavTarget>): Boolean =
        state.mode != heroMode

    override fun createFromState(baseLineState: State<NavTarget>): State<NavTarget> =
        baseLineState

    override fun createTargetState(fromState: State<NavTarget>): State<NavTarget> =
        fromState.copy(
            mode = heroMode
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.setHeroMode(
    heroMode: SpotlightHeroModel.Mode,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(SetHeroMode(heroMode, mode), animationSpec)
}
