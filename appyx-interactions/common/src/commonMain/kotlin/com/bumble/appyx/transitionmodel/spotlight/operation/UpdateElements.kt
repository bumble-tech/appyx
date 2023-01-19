package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
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
) : Operation<SpotlightModel.State<NavTarget>> {
    override fun isApplicable(state: SpotlightModel.State<NavTarget>): Boolean =
        true

    override fun invoke(baselineState: SpotlightModel.State<NavTarget>): NavTransition<SpotlightModel.State<NavTarget>> {
        val fromState = baselineState.copy(
            created = items.asElements(),
            standard = baselineState.standard
        )

        val targetState = fromState.copy(
            created = emptyList(),
            standard = fromState.created,
            destroyed = fromState.standard,
            activeIndex = initialActiveIndex ?: fromState.activeIndex,
            activeWindow = initialActiveWindow ?: fromState.activeWindow,
        )

        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> Spotlight<NavTarget>.updateElements(
    items: List<NavTarget>,
    initialActiveIndex: Float? = null,
    initialActiveWindow: Float? = null,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(
        operation = UpdateElements(
            items = items,
            initialActiveIndex = initialActiveIndex,
            initialActiveWindow = initialActiveWindow
        ),
        animationSpec = animationSpec
    )
}
