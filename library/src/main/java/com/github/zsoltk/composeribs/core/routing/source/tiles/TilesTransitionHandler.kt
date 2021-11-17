package com.github.zsoltk.composeribs.core.routing.source.tiles

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.transition.ModifierTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec

@Suppress("TransitionPropertiesLabel")
class TilesTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Tiles.TransitionState, Float> = { tween(500) }
) : ModifierTransitionHandler<T, Tiles.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Tiles.TransitionState>,
        descriptor: TransitionDescriptor<T, Tiles.TransitionState>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Tiles.TransitionState.CREATED -> 0f
                    Tiles.TransitionState.STANDARD -> 0.75f
                    Tiles.TransitionState.SELECTED -> 1.0f
                    Tiles.TransitionState.DESTROYED -> 0f
                }
            })

        val destroyProgress = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Tiles.TransitionState.DESTROYED -> 1f
                    else -> 0f
                }
            })

        offset(x = Dp(1000f * destroyProgress.value), y = Dp(-200 * destroyProgress.value))
            .rotate(720 * destroyProgress.value)
            .scale(scale.value)
    }
}
