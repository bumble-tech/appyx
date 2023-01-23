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
import androidx.compose.ui.zIndex
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpDp
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.operation.VoteLike
import com.bumble.appyx.transitionmodel.cards.operation.VotePass
import kotlin.math.roundToInt
import com.bumble.appyx.interactions.Logger


class CardsProps<NavTarget : Any>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, CardsModel.State<NavTarget>> {
    private val width = transitionBounds.widthDp.value

    private data class Props(
        val scale: Float = 1f,
        val positionalOffsetX: Dp = 0.dp,
        val rotationZ: Float = 0f,
        val zIndex: Float = 0f,
    )

    private val hidden = Props(
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
        positionalOffsetX = (-voteCardPositionMultiplier * width).dp,
        rotationZ = -45f,
        zIndex = 2f,
    )

    private val voteLike = top.copy(
        positionalOffsetX = (voteCardPositionMultiplier * width).dp,
        rotationZ = 45f,
        zIndex = 2f,
    )

    private fun <NavTarget> CardsModel.State<NavTarget>.likedList(): List<MatchedProps<NavTarget, Props>> =
        liked.lastOrNull()?.let {
            listOf(MatchedProps(it, voteLike))
        } ?: listOf()

    private fun <NavTarget> CardsModel.State<NavTarget>.passedList(): List<MatchedProps<NavTarget, Props>> =
        passed.lastOrNull()?.let {
            listOf(MatchedProps(it, votePass))
        } ?: listOf()

    private fun <NavTarget> CardsModel.State<NavTarget>.others(): List<MatchedProps<NavTarget, Props>> =
        queued.mapIndexed { index, navElement ->
            val props = when (index) {
                0 -> top
                1 -> bottom
                else -> hidden
            }

            MatchedProps(navElement, props)
        }

    private fun <NavTarget> CardsModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        likedList() + passedList() + others()

    override fun map(segment: TransitionModel.Segment<CardsModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            val scale = lerpFloat(
                start = t0.props.scale,
                end = t1.props.scale,
                progress = segment.progress
            )

            val zIndex = lerpFloat(
                start = t0.props.zIndex,
                end = t1.props.zIndex,
                progress = segment.progress
            )

            val rotationZ = lerpFloat(
                start = t0.props.rotationZ,
                end = t1.props.rotationZ,
                progress = segment.progress
            )

            val offsetX = lerpDp(
                start = t0.props.positionalOffsetX,
                end = t1.props.positionalOffsetX,
                progress = segment.progress
            )

            Logger.log("offsetx", "offsetx ${t0.props.positionalOffsetX} ${t1.props.positionalOffsetX} offset: $offsetX")

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (this.density * (offsetX.value)).roundToInt(),
                            y = 0
                        )
                    }
                    .zIndex(zIndex)
                    .graphicsLayer {
                        this.rotationZ = rotationZ
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .scale(scale),
                progress = segment.progress
            )
        }
    }

    class Gestures<NavTarget>(
        transitionBounds: TransitionBounds
    ) : GestureFactory<NavTarget, CardsModel.State<NavTarget>> {

        private val width = transitionBounds.widthPx
        private val height = transitionBounds.widthPx

        private var touchPosition: Offset? = null

        override fun onStartDrag(position: Offset) {
            touchPosition = position
        }

        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<NavTarget, CardsModel.State<NavTarget>> {
            val verticalRatio = (touchPosition?.y ?: 0f) / height // 0..1
            // For a perfect solution we should keep track where the touch is currently at, at any
            // given moment; then do the calculation in reverse, how much of a horizontal gesture
            // at that vertical position should move the cards.
            // For a good enough solution, now we only care for the initial touch position and
            // a baked in factor to account for the top of the card moving with different speed than
            // the bottom:
            // e.g. 4 = at top of the card, 2 = at the bottom, when voteCardPositionMultiplier = 2
            val dragToProgressFactor = voteCardPositionMultiplier * (2 - verticalRatio)
            Logger.log("Cards", "delta ${delta.x}")

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

    private companion object {
        private const val voteCardPositionMultiplier = 2
    }
}
