package com.bumble.appyx.transitionmodel.cards.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.transitionmodel.cards.operation.VoteLike
import com.bumble.appyx.transitionmodel.cards.operation.VotePass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

typealias InterpolatableOffset = com.bumble.appyx.interactions.core.ui.property.impl.Offset


class CardsProps<NavTarget : Any>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, CardsModel.State<NavTarget>> {
    private val width = transitionBounds.widthDp.value

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
        override val isVisible: Boolean = false
    ) : Interpolatable<Props>, HasModifier, BaseProps {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            scale.lerpTo(start.scale, end.scale, fraction)
            positionalOffsetX.lerpTo(start.positionalOffsetX, end.positionalOffsetX, fraction)
            rotationZ.lerpTo(start.rotationZ, end.rotationZ, fraction)
            zIndex.lerpTo(start.zIndex, end.zIndex, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(scale.modifier)
                .then(positionalOffsetX.modifier)
                .then(rotationZ.modifier)
                .then(zIndex.modifier)

        suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            // FIXME this should match the own animationSpec of the model (which can also be supplied
            //  from operation extension methods) rather than created here
            val animationSpec: SpringSpec<Float> = spring(
                stiffness = Spring.StiffnessVeryLow / 5,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
            onStart()
            val a1 = scope.async {
                scale.animateTo(
                    props.scale.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                )
            }
            val a2 = scope.async {
                positionalOffsetX.animateTo(
                    props.positionalOffsetX.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                )
            }
            val a3 = scope.async {
                rotationZ.animateTo(
                    props.rotationZ.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                )
            }
            val a4 = scope.async {
                zIndex.animateTo(
                    props.zIndex.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                )
            }
            awaitAll(a1, a2, a3, a4)
            onFinished()
        }
    }

    private val hidden = Props(
        scale = Scale(0f)
    )

    private val bottom = Props(
        scale = Scale(0.85f),
    )

    private val top = Props(
        scale = Scale(1f),
        zIndex = ZIndex(1f),
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
    )

    private val cache: MutableMap<String, Props> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun isAnimating(): StateFlow<Boolean> = isAnimating

    fun updateAnimationState(key: String, isAnimating: Boolean) {
        animations[key] = isAnimating
        this.isAnimating.update { isAnimating || animations.any { it.value } }
    }

    private fun <NavTarget> CardsModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> {
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

    override fun mapSegment(segment: TransitionModel.Output.Segment<CardsModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segment.progress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed { this },
                progress = segment.progress
            )
        }
    }

    class Gestures<NavTarget>(
        transitionBounds: TransitionBounds
    ) : GestureFactory<NavTarget, CardsModel.State<NavTarget>> {

        private val width = transitionBounds.widthPx
        private val height = transitionBounds.widthPx

        private var touchPosition:  Offset? = null

        override fun onStartDrag(position:  Offset) {
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

    override fun mapUpdate(update: TransitionModel.Output.Update<CardsModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val targetProps = update.targetState.toProps()

        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed {
                        LaunchedEffect(t1.props) {
                            Logger.log("TestDrive", "Animating ${t1.element} to ${t1.props}")
                            elementProps.animateTo(
                                scope = this,
                                props = t1.props,
                                onStart = { updateAnimationState(t1.element.id, true) },
                                onFinished = { updateAnimationState(t1.element.id, false) },
                            )
                        }
                        this
                    },
                progress = 1f
            )
        }
    }
}
