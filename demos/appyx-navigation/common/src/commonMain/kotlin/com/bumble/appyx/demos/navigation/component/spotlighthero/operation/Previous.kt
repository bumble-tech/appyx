package com.bumble.appyx.demos.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize


@Parcelize
class Previous<NavTarget>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<NavTarget>): Boolean =
        state.hasPrevious()

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        fromState.copy(
            activeIndex = fromState.activeIndex - 1f,
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.previous(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Previous(mode), animationSpec)
}
