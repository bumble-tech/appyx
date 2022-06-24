package com.bumble.appyx.routingsourcedemos.tiles.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.routingsourcedemos.tiles.Tiles
import com.bumble.appyx.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.core.routing.transition.TransitionSpec
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class TilesTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Tiles.TransitionState, Float> = { spring() }
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

        offset {
            IntOffset(
                x = (1000f * destroyProgress.value * this.density).roundToInt(),
                y = (-200 * destroyProgress.value * this.density).roundToInt()
            )
        }
            .rotate(720 * destroyProgress.value)
            .scale(scale.value)
    }
}

@Composable
fun <T> rememberTilesTransitionHandler(
    transitionSpec: TransitionSpec<Tiles.TransitionState, Float> = { spring() }
): ModifierTransitionHandler<T, Tiles.TransitionState> = remember {
    TilesTransitionHandler(transitionSpec)
}
