package com.bumble.appyx.navmodel2.cards.transitionhandler

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel
import com.bumble.appyx.core.navigation2.inputsource.Gesture
import com.bumble.appyx.core.navigation2.ui.RenderParams
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.core.navigation2.ui.UiProps.Companion.lerpFloat
import com.bumble.appyx.navmodel2.cards.Cards
import com.bumble.appyx.navmodel2.cards.operation.VoteLike
import com.bumble.appyx.navmodel2.cards.operation.VotePass
import kotlin.math.roundToInt


class CardsProps<NavTarget : Any>(
    transitionParams: TransitionParams
) : UiProps<NavTarget, Cards.State> {
    private val width = transitionParams.bounds.width
    private val height = transitionParams.bounds.height

    private data class Props(
        val scale: Float = 1f,
        val positionalOffsetX: Dp = 0.dp,
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

    private val votePass = top.copy(
        positionalOffsetX = -width,
        rotationZ = -90f,
        zIndex = 2f,
    )

    private val voteLike = top.copy(
        positionalOffsetX = width,
        rotationZ = 90f,
        zIndex = 2f,
    )

    private fun Cards.State.toProps(): Props =
        when (this) {
            is Cards.State.Queued -> queued
            is Cards.State.Bottom -> bottom
            is Cards.State.Top -> top
            is Cards.State.VoteLike -> voteLike
            is Cards.State.VotePass -> votePass
        }

    override fun map(segment: NavModel.Segment<NavTarget, Cards.State>): List<RenderParams<NavTarget, Cards.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.mapIndexed { index, t1 ->
            // TODO memoize per segment, as only percentage will change
            val t0 = fromState.find { it.key == t1.key }
            require(t0 != null)

            val fromProps = t0.state.toProps()
            val targetProps = t1.state.toProps()

            val dpOffsetX = lerp(
                start = fromProps.positionalOffsetX,
                stop = targetProps.positionalOffsetX,
                fraction = segment.progress
            )

            val rotationZ = lerpFloat(
                start = fromProps.rotationZ,
                end = targetProps.rotationZ,
                progress = segment.progress
            )

            val zIndex = lerpFloat(
                start = fromProps.zIndex,
                end = targetProps.zIndex,
                progress = segment.progress
            )

            val scale = lerpFloat(
                start = fromProps.scale,
                end = targetProps.scale,
                progress = segment.progress
            )

            RenderParams(
                navElement = t1,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (this.density * (dpOffsetX.value)).roundToInt(),
                            y = 0
                        )
                    }
                    .zIndex(zIndex)
                    .graphicsLayer(
                        rotationZ = rotationZ,
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    )
                    .scale(scale)
            )
        }
    }

    fun createGesture(delta: Offset, density: Density): Gesture<NavTarget, Cards.State> {
        val width = with(density) { width.toPx() }

        return if (delta.x < 0) {
            Gesture(
                operation = VotePass(),
                dragToProgress = { offset -> (offset.x / width) * -1 },
                partial = { offset, progress -> offset.copy(x = progress * width * -1) }
            )
        } else {
            Gesture(
                operation = VoteLike(),
                dragToProgress = { offset -> (offset.x / width) },
                partial = { offset, progress -> offset.copy(x = progress * width) }
            )
        }
    }

}
