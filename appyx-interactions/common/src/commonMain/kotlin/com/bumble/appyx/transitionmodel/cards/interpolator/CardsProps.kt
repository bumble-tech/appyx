package com.bumble.appyx.transitionmodel.cards.interpolator

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.transitionmodel.BaseInterpolator
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.transitionmodel.cards.operation.VoteLike
import com.bumble.appyx.transitionmodel.cards.operation.VotePass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

typealias InterpolatableOffset = com.bumble.appyx.interactions.core.ui.property.impl.Offset


class CardsProps<NavTarget : Any>(
    transitionBounds: TransitionBounds,
    coroutineScope: CoroutineScope,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseInterpolator<NavTarget, CardsModel.State<NavTarget>, CardsProps.Props>(
    defaultAnimationSpec = defaultAnimationSpec,
    coroutineScope
) {
    private val width = transitionBounds.widthDp.value

    override fun defaultProps(): Props = Props(width = width)

    class Props(
        val scale: Scale = Scale(value = 1f),
        val positionalOffsetX: InterpolatableOffset = InterpolatableOffset(
            value = DpOffset(
                0.dp,
                0.dp
            )
        ),
        val rotationZ: RotationZ = RotationZ(value = 0f),
        val zIndex: ZIndex = ZIndex(value = 0f),
        private val width: Float,
    ) : BaseProps(), HasModifier, Animatable<Props> {

        override val modifier: Modifier
            get() = Modifier
                .then(scale.modifier)
                .then(positionalOffsetX.modifier)
                .then(rotationZ.modifier)
                .then(zIndex.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            scope.launch {
                scale.snapTo(props.scale.value)
                positionalOffsetX.snapTo(props.positionalOffsetX.value)
                rotationZ.snapTo(props.rotationZ.value)
                zIndex.snapTo(props.zIndex.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            onStart()
            listOf(
                scope.async {
                    scale.animateTo(
                        props.scale.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                },
                scope.async {
                    positionalOffsetX.animateTo(
                        props.positionalOffsetX.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                },
                scope.async {
                    rotationZ.animateTo(
                        props.rotationZ.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                },
                scope.async {
                    zIndex.animateTo(
                        props.zIndex.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                }).awaitAll()
            onFinished()
        }

        override fun isVisible(): Boolean =
            scale.value >= 0.0f && positionalOffsetX.value.x > (-voteCardPositionMultiplier * width).dp &&
                    positionalOffsetX.value.x < (voteCardPositionMultiplier * width).dp

        override fun lerpTo(scope: CoroutineScope, start: Props, end: Props, fraction: Float) {
            scope.launch {
                scale.lerpTo(start.scale, end.scale, fraction)
                positionalOffsetX.lerpTo(start.positionalOffsetX, end.positionalOffsetX, fraction)
                rotationZ.lerpTo(start.rotationZ, end.rotationZ, fraction)
                zIndex.lerpTo(start.zIndex, end.zIndex, fraction)
                updateVisibilityState()
            }
        }
    }

    private val hidden = Props(
        scale = Scale(0f),
        width = width
    )

    private val bottom = Props(
        scale = Scale(0.85f),
        width = width
    )

    private val top = Props(
        scale = Scale(1f),
        zIndex = ZIndex(1f),
        width = width
    )

    private val votePass = Props(
        positionalOffsetX = InterpolatableOffset(
            DpOffset(
                (-voteCardPositionMultiplier * width).dp,
                0.dp
            )
        ),
        scale = Scale(1f),
        zIndex = ZIndex(2f),
        rotationZ = RotationZ(-45f),
        width = width
    )

    private val voteLike = Props(
        positionalOffsetX = InterpolatableOffset(
            DpOffset(
                (voteCardPositionMultiplier * width).dp,
                0.dp
            )
        ),
        scale = Scale(1f),
        zIndex = ZIndex(2f),
        rotationZ = RotationZ(45f),
        width = width
    )

    override fun CardsModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> {
        val result = mutableListOf<MatchedProps<NavTarget, Props>>()
        (votedCards + visibleCards + queued).map {
            when (it) {
                is CardsModel.State.Card.InvisibleCard.VotedCard -> {
                    result.add(
                        if (it.votedCardState == LIKED) {
                            MatchedProps(it.navElement, voteLike)
                        } else {
                            MatchedProps(it.navElement, votePass)
                        }
                    )
                }

                is CardsModel.State.Card.VisibleCard.TopCard -> {
                    result.add(MatchedProps(it.navElement, top))
                }

                is CardsModel.State.Card.VisibleCard.BottomCard -> {
                    result.add(MatchedProps(it.navElement, bottom))
                }

                is CardsModel.State.Card.InvisibleCard.Queued -> {
                    result.add(MatchedProps(it.navElement, hidden))
                }
            }
        }

        return result
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
                    operation = VotePass(Operation.Mode.KEYFRAME),
                    dragToProgress = { offset -> offset.x / (dragToProgressFactor * width) * -1 },
                    partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                )
            } else {
                Gesture(
                    operation = VoteLike(Operation.Mode.KEYFRAME),
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
