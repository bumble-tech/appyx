package com.bumble.appyx.components.experimental.cards.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.components.experimental.cards.operation.VoteLike
import com.bumble.appyx.components.experimental.cards.operation.VotePass
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragHorizontalDirection
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import com.bumble.appyx.utils.multiplatform.AppyxLogger

@Suppress("MagicNumber")
class CardsVisualisation<InteractionTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<InteractionTarget, CardsModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val hidden = TargetUiState(
        scale = Scale.Target(0f)
    )

    private val bottom = TargetUiState(
        scale = Scale.Target(0.85f)
    )

    private val top = TargetUiState(
        scale = Scale.Target(1f),
        zIndex = ZIndex.Target(1f),
    )

    private val votePass = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            BiasAlignment.OutsideAlignment(-voteCardPositionMultiplier, 0)
        ),
        scale = Scale.Target(1f),
        zIndex = ZIndex.Target(2f),
        rotationZ = RotationZ.Target(-45f)
    )

    private val voteLike = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            BiasAlignment.OutsideAlignment(voteCardPositionMultiplier, 0)
        ),
        scale = Scale.Target(1f),
        zIndex = ZIndex.Target(2f),
        rotationZ = RotationZ.Target(45f)
    )

    override fun CardsModel.State<InteractionTarget>.toUiTargets():
            List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        val result = mutableListOf<MatchedTargetUiState<InteractionTarget, TargetUiState>>()
        (votedCards + visibleCards + queued).map {
            when (it) {
                is CardsModel.State.Card.InvisibleCard.VotedCard -> {
                    result.add(
                        if (it.votedCardState == LIKED) {
                            MatchedTargetUiState(it.element, voteLike)
                        } else {
                            MatchedTargetUiState(it.element, votePass)
                        }
                    )
                }

                is CardsModel.State.Card.VisibleCard.TopCard -> {
                    result.add(MatchedTargetUiState(it.element, top))
                }

                is CardsModel.State.Card.VisibleCard.BottomCard -> {
                    result.add(MatchedTargetUiState(it.element, bottom))
                }

                is CardsModel.State.Card.InvisibleCard.Queued -> {
                    result.add(MatchedTargetUiState(it.element, hidden))
                }
            }
        }

        return result
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)


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
            state: CardsModel.State<InteractionTarget>,
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
            AppyxLogger.d("Cards", "delta ${delta.x}")

            return if (dragHorizontalDirection(delta) == Drag.HorizontalDirection.LEFT) {
                Gesture(
                    operation = VotePass(),
                    completeAt = Offset(-dragToProgressFactor * width, 0f)
                )
            } else {
                Gesture(
                    operation = VoteLike(),
                    completeAt = Offset(dragToProgressFactor * width, 0f)
                )
            }
        }
    }

    private companion object {
        private const val voteCardPositionMultiplier = 2
    }
}
