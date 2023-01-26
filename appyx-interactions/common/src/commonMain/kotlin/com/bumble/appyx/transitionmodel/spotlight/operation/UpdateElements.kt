package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.asElements
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel

@Parcelize
class UpdateElements<NavTarget : Any>(
    private val items: @RawValue List<NavTarget>,
    private val initialActiveIndex: Float? = null,
    private val initialActiveWindow: Float? = null,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<SpotlightModel.State<NavTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(baseLineState: SpotlightModel.State<NavTarget>): SpotlightModel.State<NavTarget> =
        baseLineState.copy(
            created = items.asElements(),
            standard = baseLineState.standard
        )

    override fun createTargetState(fromState: SpotlightModel.State<NavTarget>): SpotlightModel.State<NavTarget> =
        fromState.copy(
            created = emptyList(),
            standard = fromState.created,
            destroyed = fromState.standard,
            activeIndex = initialActiveIndex ?: fromState.activeIndex,
            activeWindow = initialActiveWindow ?: fromState.activeWindow,
        )
}

fun <NavTarget : Any> Spotlight<NavTarget>.updateElements(
    items: List<NavTarget>,
    initialActiveIndex: Float? = null,
    initialActiveWindow: Float? = null,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(
        operation = UpdateElements(
            items = items,
            initialActiveIndex = initialActiveIndex,
            initialActiveWindow = initialActiveWindow,
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
