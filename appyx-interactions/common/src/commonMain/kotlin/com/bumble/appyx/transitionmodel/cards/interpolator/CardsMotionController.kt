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
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.transitionmodel.cards.interpolator.CardsMotionController.UiState
import com.bumble.appyx.transitionmodel.cards.operation.VoteLike
import com.bumble.appyx.transitionmodel.cards.operation.VotePass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CardsMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, CardsModel.State<InteractionTarget>, UiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    override fun defaultUiState(uiContext: UiContext, initialUiState: UiState?): UiState = UiState(uiContext = uiContext)

    class UiState(
        val uiContext: UiContext,
        val scale: Scale = Scale(value = 1f),
        val positionalOffsetX: Position = Position(
            initialOffset = DpOffset(
                0.dp,
                0.dp
            ),
        ),
        val rotationZ: RotationZ = RotationZ(value = 0f),
        val zIndex: ZIndex = ZIndex(value = 0f)
    ) : BaseUiState<UiState>(
        motionProperties = listOf(scale, positionalOffsetX),
        coroutineScope = uiContext.coroutineScope
    ) {

        override val modifier: Modifier
            get() = Modifier
                .then(scale.modifier)
                .then(positionalOffsetX.modifier)
                .then(rotationZ.modifier)
                .then(zIndex.modifier)

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                scale.snapTo(uiState.scale.value)
                positionalOffsetX.snapTo(uiState.positionalOffsetX.value)
                rotationZ.snapTo(uiState.rotationZ.value)
                zIndex.snapTo(uiState.zIndex.value)
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            listOf(
                scope.async {
                    scale.animateTo(
                        uiState.scale.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                },
                scope.async {
                    positionalOffsetX.animateTo(
                        uiState.positionalOffsetX.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                },
                scope.async {
                    rotationZ.animateTo(
                        uiState.rotationZ.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                },
                scope.async {
                    zIndex.animateTo(
                        uiState.zIndex.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                }).awaitAll()
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                scale.lerpTo(start.scale, end.scale, fraction)
                positionalOffsetX.lerpTo(start.positionalOffsetX, end.positionalOffsetX, fraction)
                rotationZ.lerpTo(start.rotationZ, end.rotationZ, fraction)
                zIndex.lerpTo(start.zIndex, end.zIndex, fraction)
            }
        }
    }

    private val hidden = UiState(
        uiContext = uiContext,
        scale = Scale(0f)
    )

    private val bottom = UiState(
        uiContext = uiContext,
        scale = Scale(0.85f)
    )

    private val top = UiState(
        uiContext = uiContext,
        scale = Scale(1f),
        zIndex = ZIndex(1f),
    )

    private val votePass = UiState(
        uiContext = uiContext,
        positionalOffsetX = Position(
            initialOffset = DpOffset(
                (-voteCardPositionMultiplier * uiContext.transitionBounds.widthDp.value).dp,
                0.dp
            ),
        ),
        scale = Scale(1f),
        zIndex = ZIndex(2f),
        rotationZ = RotationZ(-45f)
    )

    private val voteLike = UiState(
        uiContext = uiContext,
        positionalOffsetX = Position(
            initialOffset = DpOffset(
                (voteCardPositionMultiplier * uiContext.transitionBounds.widthDp.value).dp,
                0.dp
            ),
        ),
        scale = Scale(1f),
        zIndex = ZIndex(2f),
        rotationZ = RotationZ(45f)
    )

    override fun CardsModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> {
        val result = mutableListOf<MatchedUiState<InteractionTarget, UiState>>()
        (votedCards + visibleCards + queued).map {
            when (it) {
                is CardsModel.State.Card.InvisibleCard.VotedCard -> {
                    result.add(
                        if (it.votedCardState == LIKED) {
                            MatchedUiState(it.element, voteLike)
                        } else {
                            MatchedUiState(it.element, votePass)
                        }
                    )
                }

                is CardsModel.State.Card.VisibleCard.TopCard -> {
                    result.add(MatchedUiState(it.element, top))
                }

                is CardsModel.State.Card.VisibleCard.BottomCard -> {
                    result.add(MatchedUiState(it.element, bottom))
                }

                is CardsModel.State.Card.InvisibleCard.Queued -> {
                    result.add(MatchedUiState(it.element, hidden))
                }
            }
        }

        return result
    }

    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds
    ) : GestureFactory<InteractionTarget, CardsModel.State<InteractionTarget>> {

        private val width = transitionBounds.widthPx
        private val height = transitionBounds.widthPx

        private var touchPosition: Offset? = null

        override fun onStartDrag(position: Offset) {
            touchPosition = position
        }

        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, CardsModel.State<InteractionTarget>> {
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
