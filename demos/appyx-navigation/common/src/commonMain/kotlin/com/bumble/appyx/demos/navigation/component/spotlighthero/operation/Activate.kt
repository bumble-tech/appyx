package com.bumble.appyx.demos.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Activate<NavTarget : Any>(
    private val index: Float,
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<NavTarget>): Boolean =
        index != state.activeIndex &&
                (index in 0f..state.positions.lastIndex.toFloat())

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        fromState.copy(
            activeIndex = index,
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.activate(
    index: Float,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Activate(index, mode), animationSpec)
}
