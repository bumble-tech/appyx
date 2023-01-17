package com.bumble.appyx.transitionmodel.cards.interpolator

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
import com.bumble.appyx.interactions.core.NavModel
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.RenderParams
import com.bumble.appyx.interactions.core.ui.TransitionParams
import com.bumble.appyx.interactions.core.ui.UiProps
import com.bumble.appyx.interactions.core.ui.UiProps.Companion.lerpFloat
import com.bumble.appyx.transitionmodel.cards.Cards
import com.bumble.appyx.transitionmodel.cards.operation.VoteLike
import com.bumble.appyx.transitionmodel.cards.operation.VotePass
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

    private val voteCardPositionMultiplier = 2

    private val votePass = top.copy(
        positionalOffsetX = (-voteCardPositionMultiplier * width.value).dp,
        rotationZ = -45f,
        zIndex = 2f,
    )

    private val voteLike = top.copy(
        positionalOffsetX = (voteCardPositionMultiplier * width.value).dp,
        rotationZ = 45f,
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

    fun createGesture(touchPosition: Offset, delta: Offset, density: Density): Gesture<NavTarget, Cards.State> {
        val width = with(density) { width.toPx() }
        val height = with(density) { height.toPx() }
        val verticalRatio = touchPosition.y / height // 0..1
        // For a perfect solution we should keep track where the touch is currently at, at any
        // given moment; then do the calculation in reverse, how much of a horizontal gesture
        // at that vertical position should move the cards.
        // For a good enough solution, now we only care for the initial touch position and
        // a baked in factor to account for the top of the card moving with different speed than
        // the bottom:
        // e.g. 4 = at top of the card, 2 = at the bottom, when voteCardPositionMultiplier = 2
        val dragToProgressFactor = voteCardPositionMultiplier * (2 - verticalRatio)

        return if (delta.x < 0) {
            Gesture(
                operation = VotePass(),
                dragToProgress = { offset -> offset.x / (dragToProgressFactor * width) * -1 },
                partial = { offset, progress -> offset.copy(x = progress * width * -1) }
            )
        } else {
            Gesture(
                operation = VoteLike(),
                dragToProgress = { offset -> offset.x / (dragToProgressFactor * width) },
                partial = { offset, progress -> offset.copy(x = progress * width) }
            )
        }
    }

}
