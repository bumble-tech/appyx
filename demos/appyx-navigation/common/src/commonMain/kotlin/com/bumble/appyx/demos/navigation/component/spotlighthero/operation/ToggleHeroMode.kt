package com.bumble.appyx.demos.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class ToggleHeroMode<NavTarget>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<NavTarget>>() {

    override fun isApplicable(state: State<NavTarget>): Boolean =
        true

    override fun createFromState(baseLineState: State<NavTarget>): State<NavTarget> =
        baseLineState

    override fun createTargetState(fromState: State<NavTarget>): State<NavTarget> =
        fromState.copy(
            mode = when (fromState.mode) {
                LIST -> HERO
                HERO -> LIST
            }
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.toggleHeroMode(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(ToggleHeroMode(mode), animationSpec)
}
