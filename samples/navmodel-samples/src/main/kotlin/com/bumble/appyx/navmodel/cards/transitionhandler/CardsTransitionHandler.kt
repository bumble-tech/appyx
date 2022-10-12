package com.bumble.appyx.navmodel.cards.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.State.Bottom
import com.bumble.appyx.navmodel.cards.Cards.State.IndicateLike
import com.bumble.appyx.navmodel.cards.Cards.State.IndicatePass
import com.bumble.appyx.navmodel.cards.Cards.State.Queued
import com.bumble.appyx.navmodel.cards.Cards.State.Top
import com.bumble.appyx.navmodel.cards.Cards.State.VoteLike
import com.bumble.appyx.navmodel.cards.Cards.State.VotePass
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel", "MagicNumber")
class CardsTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Cards.State, Float> = { spring(stiffness = Spring.StiffnessVeryLow) }
) : ModifierTransitionHandler<T, Cards.State>() {

    private data class Props(
        val scale: Float = 1f,
        val positionalOffsetX: Dp = 0.dp,
        val angularOffset: Float = 0f,
        val rotationZ: Float = 0f,
        val zIndex: Float = 0f,
    )

    private val queued = Props(
        scale = 0f
    )

    private val bottom = Props(
        scale = 0.85f,
    )

    private val top = Props(
        scale = 1f,
        zIndex = 1f,
    )

    private val indicateLike = top.copy(
        angularOffset = 10f,
        rotationZ = 10f,
        zIndex = 2f,
    )

    private val indicatePass = top.copy(
        angularOffset = -10f,
        rotationZ = -10f,
        zIndex = 2f,
    )

    private val votePass = top.copy(
        positionalOffsetX = -(1500).dp,
        rotationZ = -270f,
        zIndex = 2f,
    )

    private val voteLike = top.copy(
        positionalOffsetX = 1500.dp,
        rotationZ = 270f,
        zIndex = 2f,
    )

    @SuppressLint("UnusedTransitionTargetStateParameter")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Cards.State>,
        descriptor: TransitionDescriptor<T, Cards.State>
    ): Modifier = modifier.composed {

        val halfHeightDp = (descriptor.params.bounds.height.value) / 2
        val oneAndHalfHeightDp = halfHeightDp * 3f

        val angularOffsetDegrees = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.toProps().angularOffset }
        )

        // If we only rotated the cards at the indicate vote states,
        // they'd just rotate around their own center.
        // We'll also want to offset the cards on an arc for a nice visual effect.
        val angularOffset = remember { derivedStateOf {
            val angleRadians = Math.toRadians(angularOffsetDegrees.value.toDouble() - 90)
            // The center of rotation is at 150% screen height (below the bottom screen edge)
            val x = oneAndHalfHeightDp * cos(angleRadians)
            val y = oneAndHalfHeightDp * sin(angleRadians)
            Offset(x.toFloat(), y.toFloat())
        }}

        val rotationZ = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.toProps().rotationZ })

        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.toProps().scale })

        val dpOffsetX = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.toProps().positionalOffsetX.value })

        val zIndex = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.toProps().zIndex })

        return@composed this
            .offset {
                IntOffset(
                    x = (this.density * (dpOffsetX.value + angularOffset.value.x)).roundToInt(),
                    y = (this.density * (oneAndHalfHeightDp + angularOffset.value.y)).roundToInt()
                )
            }
            .zIndex(zIndex.value)
            .graphicsLayer(
                rotationZ = rotationZ.value
            )
            .scale(scale.value)
    }

        @Composable
        private fun Cards.State.toProps() =
            when (this) {
                is Queued -> queued
                is Bottom -> bottom
                is Top -> top
                is IndicateLike -> indicateLike
                is IndicatePass -> indicatePass
                is VoteLike -> voteLike
                is VotePass -> votePass
            }
    }

@Composable
fun <T> rememberCardsTransitionHandler(
    transitionSpec: TransitionSpec<Cards.State, Float> = { spring(stiffness = Spring.StiffnessVeryLow) }
): ModifierTransitionHandler<T, Cards.State> = remember {
    CardsTransitionHandler(transitionSpec)
}
