package com.bumble.appyx.navigation.component.spotlighthero.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHero
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Next<NavTarget>(
    override var mode: Operation.Mode = Operation.Mode.IMPOSED
) : BaseOperation<SpotlightHeroModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightHeroModel.State<NavTarget>): Boolean =
        state.hasNext()

    override fun createFromState(
        baseLineState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(
        fromState: SpotlightHeroModel.State<NavTarget>
    ): SpotlightHeroModel.State<NavTarget> =
        fromState.copy(
            activeIndex = fromState.activeIndex + 1f
        )
}

fun <NavTarget : Any> SpotlightHero<NavTarget>.next(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMPOSED
) {
    operation(Next(mode), animationSpec)
}
