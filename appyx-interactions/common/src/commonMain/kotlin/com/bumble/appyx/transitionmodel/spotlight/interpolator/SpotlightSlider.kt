package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.mapState
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

class SpotlightSlider<InteractionTarget : Any>(
    private val uiContext: UiContext,
    override val clipToBounds: Boolean = false,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : BaseMotionController<InteractionTarget, SpotlightModel.State<InteractionTarget>, SpotlightSlider.UiState>(
    uiContext = uiContext
) {
    private val width = uiContext.transitionBounds.widthDp
    private val geometry = GenericFloatProperty(0f) // TODO sync this with the model's initial value

    override val geometryMappings: List<Pair<(SpotlightModel.State<InteractionTarget>) -> Float, MotionProperty<Float, AnimationVector1D>>> =
        listOf(
            { state: SpotlightModel.State<InteractionTarget> -> state.activeIndex } to geometry
        )

    data class UiState(
        val uiContext: UiContext,
        val position: Position,
        val scale: Scale = Scale(1f),
        val alpha: Alpha = Alpha(1f),
        val zIndex: Float = 1f,
        val aspectRatio: Float = 0.42f,
        val rotation: Float = 0f,
    ) : BaseUiState<UiState>(
        motionProperties = listOf(scale, alpha, position),
        coroutineScope = uiContext.coroutineScope
    ) {

        override val modifier: Modifier
            get() = Modifier
                .then(position.modifier)
                .then(alpha.modifier)
                .then(scale.modifier)

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                position.snapTo(uiState.position.value)
                alpha.snapTo(uiState.alpha.value)
                scale.snapTo(uiState.scale.value)
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            scope.launch {
                listOf(
                    scope.async {
                        position.animateTo(
                            uiState.position.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        )
                        alpha.animateTo(
                            uiState.alpha.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        )
                        scale.animateTo(
                            uiState.scale.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        )
                    }
                ).awaitAll()
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                position.lerpTo(start.position, end.position, fraction)
            }
        }
    }

    var position = 0

    override fun defaultUiState(uiContext: UiContext, initialUiState: UiState?): UiState = UiState(
        position = Position(
            initialOffset = initialUiState?.position?.initialOffset ?: DpOffset.Zero,
            clipToBounds = clipToBounds,
            bounds = uiContext.transitionBounds,
            displacement = geometry.valueFlow
                .mapState(uiContext.coroutineScope) { value ->
                    DpOffset((value * width.value).dp, 0.dp)
                },
        ),
        uiContext = uiContext
    )

    private val created = defaultUiState(uiContext, null).copy(
        position = Position(
            DpOffset(0.dp, width),
        ),
        scale = Scale(0f),
        alpha = Alpha(1f),
        zIndex = 0f,
        aspectRatio = 1f,
    )

    private val standard = defaultUiState(uiContext, null).copy(
        position = Position(DpOffset.Zero)
    )

    private val destroyed = defaultUiState(uiContext, null).copy(
        position = Position(
            DpOffset(0.dp, -width)
        ),
        scale = Scale(0f),
        alpha = Alpha(0f),
        zIndex = -1f,
        aspectRatio = 1f,
        rotation = 360f
    )

    override fun SpotlightModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                val target = it.value.toProps()
                MatchedUiState(
                    element = it.key,
                    uiState = UiState(
                        position = Position(
                            DpOffset(
                                dpOffset(index).x,
                                target.position.value.y
                            )
                        ),
                        scale = target.scale,
                        alpha = target.alpha,
                        zIndex = target.zIndex,
                        rotation = target.rotation,
                        aspectRatio = target.aspectRatio,
                        uiContext = uiContext,
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

