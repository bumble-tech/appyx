package com.bumble.appyx.navmodel.tiles.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
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
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.tiles.Tiles
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class TilesTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Tiles.State, Float> = { spring() }
) : ModifierTransitionHandler<T, Tiles.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Tiles.State>,
        descriptor: TransitionDescriptor<T, Tiles.State>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Tiles.State.CREATED -> 0f
                    Tiles.State.STANDARD -> 0.75f
                    Tiles.State.SELECTED -> 1.0f
                    Tiles.State.DESTROYED -> 0f
                }
            })

        val destroyProgress = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Tiles.State.DESTROYED -> 1f
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
    transitionSpec: TransitionSpec<Tiles.State, Float> = { spring(stiffness = Spring.StiffnessVeryLow) }
): ModifierTransitionHandler<T, Tiles.State> = remember {
    TilesTransitionHandler(transitionSpec)
}
