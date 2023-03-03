package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.output.BaseUiState
import com.bumble.appyx.interactions.core.ui.output.MatchedUiState
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.transitionmodel.spotlight.operation.Next
import com.bumble.appyx.transitionmodel.spotlight.operation.Previous
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.animation.core.Animatable as Animatable1

typealias OffsetP = com.bumble.appyx.interactions.core.ui.property.impl.Offset

class SpotlightSlider<InteractionTarget : Any>(
    uiContext: UiContext,
    override val clipToBounds: Boolean = false,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : BaseMotionController<InteractionTarget, SpotlightModel.State<InteractionTarget>, SpotlightSlider.UiState>(
    scope = uiContext.coroutineScope
) {
    private val screenWidth = uiContext.transitionBounds.screenWidthDp
    private val transitionBounds = uiContext.transitionBounds
    private val width = uiContext.transitionBounds.widthDp
    private val height = uiContext.transitionBounds.heightDp
    private val scroll = Animatable1(0f) // TODO sync this with the model's initial value

    override val geometryMappings: List<Pair<(SpotlightModel.State<InteractionTarget>) -> Float, Animatable1<Float, AnimationVector1D>>> =
        listOf(
            { state: SpotlightModel.State<InteractionTarget> -> state.activeIndex } to scroll
        )

    data class UiState(
        val offset: OffsetP,
        val scale: Scale = Scale(1f),
        val alpha: Alpha = Alpha(1f),
        val zIndex: Float = 1f,
        val aspectRatio: Float = 0.42f,
        val rotation: Float = 0f,
        val clipToBounds: Boolean,
        private val containerWidth: Dp,
        private val screenWidth: Dp,
        private val transitionBounds: TransitionBounds
    ) : BaseUiState(listOf(offset.isAnimating, scale.isAnimating, alpha.isAnimating)),
        Animatable<UiState> {

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(alpha.modifier)
                .then(scale.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: UiState) {
            scope.launch {
                offset.snapTo(props.offset.value)
                alpha.snapTo(props.alpha.value)
                scale.snapTo(props.scale.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            scope.launch {
                listOf(
                    scope.async {
                        offset.animateTo(
                            props.offset.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        ) {
                            updateVisibilityState()
                        }
                        alpha.animateTo(
                            props.alpha.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        ) {
                            updateVisibilityState()
                        }
                        scale.animateTo(
                            props.scale.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        ) {
                            updateVisibilityState()
                        }
                    }
                ).awaitAll()
            }
        }

        override fun isVisible(): Boolean {
            val leftEdgeOffsetDp = offset.displacedValue.value.x.value.roundToInt()
            val rightEdgeOffsetDp = leftEdgeOffsetDp + containerWidth.value.roundToInt()
            val visibleWindowLeftEdge = calculatedWindowLeftEdge(clipToBounds)
            val visibleWindowRightEdge = calculateWindowRightEdge()
            return (rightEdgeOffsetDp <= visibleWindowLeftEdge || leftEdgeOffsetDp >= visibleWindowRightEdge).not()
        }

        private fun calculateWindowRightEdge() = if (clipToBounds) {
            containerWidth.value.roundToInt()
        } else {
            (screenWidth - transitionBounds.containerOffsetX).value.roundToInt()
        }

        private fun calculatedWindowLeftEdge(clipToBounds: Boolean): Int {
            return if (clipToBounds) {
                0
            } else {
                -transitionBounds.containerOffsetX.value.roundToInt()
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                updateVisibilityState()
            }
        }
    }

    override fun defaultUiState(): UiState = UiState(
        offset = OffsetP(DpOffset.Zero).also {
            it.displacement = derivedStateOf {
                DpOffset((scroll.value * width.value).dp, 0.dp)
            }
        },
        screenWidth = screenWidth,
        containerWidth = width,
        clipToBounds = clipToBounds,
        transitionBounds = transitionBounds
    )

    private val created = defaultUiState().copy(
        offset = OffsetP(DpOffset(0.dp, width)),
        scale = Scale(0f),
        alpha = Alpha(1f),
        zIndex = 0f,
        aspectRatio = 1f,
        screenWidth = width
    )

    private val standard = defaultUiState().copy(
        offset = OffsetP(DpOffset.Zero),
        screenWidth = width
    )

    private val destroyed = defaultUiState().copy(
        offset = OffsetP(DpOffset(0.dp, -width)),
        scale = Scale(0f),
        alpha = Alpha(0f),
        zIndex = -1f,
        aspectRatio = 1f,
        rotation = 360f,
        screenWidth = width
    )

    override fun SpotlightModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                val target = it.value.toProps()
                MatchedUiState(
                    element = it.key,
                    uiState = UiState(
                        offset = OffsetP(
                            DpOffset(
                                dpOffset(index).x,
                                target.offset.value.y
                            )
                        ),
                        scale = target.scale,
                        alpha = target.alpha,
                        zIndex = target.zIndex,
                        rotation = target.rotation,
                        aspectRatio = target.aspectRatio,
                        screenWidth = width,
                        containerWidth = width,
                        clipToBounds = clipToBounds,
                        transitionBounds = transitionBounds
                    )
                )
            }
        }
    }

    fun SpotlightModel.State.ElementState.toProps() =
        when (this) {
            CREATED -> created
            STANDARD -> standard
            DESTROYED -> destroyed
        }

    private fun dpOffset(
        index: Int
    ) = DpOffset(
        x = (index * width.value).dp,
        y = 0.dp
    )

    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
        private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
    ) : GestureFactory<InteractionTarget, SpotlightModel.State<InteractionTarget>> {
        private val width = transitionBounds.widthPx
        private val height = transitionBounds.heightPx

        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, SpotlightModel.State<InteractionTarget>> {
            return when (orientation) {
                Orientation.Horizontal -> if (delta.x < 0) {
                    Gesture(
                        operation = Next(KEYFRAME),
                        dragToProgress = { offset -> (offset.x / width) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(KEYFRAME),
                        dragToProgress = { offset -> (offset.x / width) },
                        partial = { offset, partial -> offset.copy(x = partial * width) }
                    )
                }
                Orientation.Vertical -> if (delta.y < 0) {
                    Gesture(
                        operation = Next(KEYFRAME),
                        dragToProgress = { offset -> (offset.y / height) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * height * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(KEYFRAME),
                        dragToProgress = { offset -> (offset.y / height) },
                        partial = { offset, partial -> offset.copy(y = partial * height) }
                    )
                }
            }
        }
    }


    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}

