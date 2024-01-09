package com.bumble.appyx.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.utils.multiplatform.Parcelize


@Parcelize
class Last<NavTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        fromState.copy(
            activeIndex = fromState.positions.lastIndex.toFloat(),
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.last(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Last(mode), animationSpec)
}
