package com.bumble.appyx.transitionmodel

import DefaultAnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.withPrevious
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseMotionController<InteractionTarget : Any, ModelState, UiState>(
    private val uiContext: UiContext,
    protected val defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
) : MotionController<InteractionTarget, ModelState> where UiState : BaseUiState<UiState> {

    private val coroutineScope = uiContext.coroutineScope
    private val uiStateCache: MutableMap<String, UiState> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected var currentSpringSpec: SpringSpec<Float> = defaultAnimationSpec

    open val geometryMappings: List<Pair<(ModelState) -> Float, MotionProperty<Float, AnimationVector1D>>> =
        emptyList()


    private val _finishedAnimations = MutableSharedFlow<Element<InteractionTarget>>()
    override val finishedAnimations: Flow<Element<InteractionTarget>> = _finishedAnimations

    abstract fun defaultUiState(uiContext: UiContext): UiState

    override fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        currentSpringSpec = springSpec
    }

    final override fun isAnimating(): StateFlow<Boolean> =
        isAnimating

    // TODO extract
    abstract fun ModelState.toUiState(): List<MatchedUiState<InteractionTarget, UiState>>

    override fun mapUpdate(
        update: Update<ModelState>
    ): List<ElementUiModel<InteractionTarget>> {
        val targetProps = update.currentTargetState.toUiState()

        coroutineScope.launch {
            updateGeometry(update)
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = uiStateCache.getOrPut(t1.element.id) { defaultUiState(uiContext) }
            ElementUiModel(
                element = t1.element,
                visibleState = elementProps.isVisible,
                animationContainer = @Composable {
                    observeElementAnimationChanges(elementProps, t1)
                    manageAnimations(elementProps, t1, update)
                },
                modifier = elementProps.modifier,
                progress = MutableStateFlow(1f),
            )
        }
    }

    @Composable
    private fun manageAnimations(
        elementProps: UiState,
        targetProps: MatchedUiState<InteractionTarget, UiState>,
        update: Update<ModelState>
    ) {
        LaunchedEffect(update, this) {
            // make sure to use scope created by Launched effect as this scope should be cancelled
            // when associated FrameModel cease to exist
            launch {
                if (update.animate) {
                    elementProps.animateTo(
                        scope = this,
                        uiState = targetProps.uiState,
                        springSpec = currentSpringSpec,
                    )
                } else {
                    elementProps.snapTo(this, targetProps.uiState)
                }
            }
        }
    }

    @Composable
    private fun observeElementAnimationChanges(
        elementProps: UiState,
        targetProps: MatchedUiState<InteractionTarget, UiState>
    ) {
        LaunchedEffect(this) {
            // make sure to use scope created by Launched effect as this scope should be cancelled
            // when associated FrameModel cease to exist
            launch {
                elementProps.isAnimating
                    .distinctUntilChanged()
                    .withPrevious()
                    .collect { values ->
                        val previous = values.previous ?: return@collect
                        val current = values.current
                        if (current && !previous) {
                            // animation started
                            animations[targetProps.element.id] = true
                            isAnimating.update { true }
                            Logger.log(
                                this@BaseMotionController.javaClass.simpleName,
                                "animation for element ${targetProps.element.id} is started"
                            )
                        } else {
                            // animation finished
                            _finishedAnimations.emit(targetProps.element)
                            animations[targetProps.element.id] = false
                            isAnimating.update { animations.any { it.value } }
                            Logger.log(
                                this@BaseMotionController.javaClass.simpleName,
                                "animation for element ${targetProps.element.id} is finished"
                            )
                        }
                    }
            }
        }
    }

    private suspend fun updateGeometry(update: Update<ModelState>) {
        geometryMappings.forEach { (fieldOfState, geometry) ->
            val targetValue = fieldOfState(update.currentTargetState)
            geometry.animateTo(
                targetValue,
                spring(
                    stiffness = currentSpringSpec.stiffness,
                    dampingRatio = currentSpringSpec.dampingRatio
                )
            ) {
                Logger.log(
                    this@BaseMotionController.javaClass.simpleName,
                    "Geometry animateTo (Update) – $targetValue"
                )
            }
        }
    }

    override fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<ElementUiModel<InteractionTarget>> {
        val (fromState, targetState) = segment.stateTransition
        val fromProps = fromState.toUiState()
        val targetProps = targetState.toUiState()

        coroutineScope.launch {
            segmentProgress.collect {
                updateGeometry(segment, it)
            }
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementUiState = uiStateCache.getOrPut(t1.element.id) { defaultUiState(uiContext) }
            //Synchronously apply current value to props before they reach composition to avoid jumping between default & current valu
            elementUiState.lerpTo(coroutineScope, t0.uiState, t1.uiState, initialProgress)

            ElementUiModel(
                element = t1.element,
                visibleState = elementUiState.isVisible,
                animationContainer = @Composable {
                    interpolatedProps(segmentProgress, elementUiState, t0, t1, initialProgress)
                },
                modifier = elementUiState.modifier,
                progress = segmentProgress,
            )
        }
    }

    @Composable
    private fun interpolatedProps(
        segmentProgress: Flow<Float>,
        elementProps: UiState,
        from: MatchedUiState<InteractionTarget, UiState>,
        to: MatchedUiState<InteractionTarget, UiState>,
        initialProgress: Float
    ) {
        val progress by segmentProgress.collectAsState(initialProgress)
        LaunchedEffect(progress) {
            elementProps.lerpTo(coroutineScope, from.uiState, to.uiState, progress)
        }
    }

    private suspend fun updateGeometry(
        segment: Segment<ModelState>,
        segmentProgress: Float
    ) {
        geometryMappings.forEach { (fieldOfState, geometry) ->
            val (behaviour, targetValue) = geometryTargetValue(
                segment,
                segmentProgress,
                fieldOfState
            )

            when (behaviour) {
                GeometryBehaviour.SNAP -> {
                    geometry.snapTo(targetValue)
                    Logger.log(
                        this@BaseMotionController.javaClass.simpleName,
                        "Geometry snapTo (Segment): $targetValue"
                    )
                }

                GeometryBehaviour.ANIMATE -> {
                    if (geometry.value != targetValue) {
                        geometry.animateTo(
                            targetValue, spring(
                                stiffness = currentSpringSpec.stiffness,
                                dampingRatio = currentSpringSpec.dampingRatio
                            )
                        ) {
                            Logger.log(
                                this@BaseMotionController.javaClass.simpleName,
                                "Geometry animateTo (Segment) – ${geometry.value} -> $targetValue"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun geometryTargetValue(
        segment: Segment<ModelState>,
        segmentProgress: Float,
        fieldOfState: (ModelState) -> Float
    ): Pair<GeometryBehaviour, Float> {
        val fromValue = fieldOfState(segment.fromState)
        val targetValue = fieldOfState(segment.targetState)

        // If the geometry value was inserted via a GEOMETRY operation mode, then it was applied both to the
        // start and end values (see [BaseTransitionModel.updateGeometry()], and they should be the same.
        // This means that even though we have a Segment, the interpolation is working on some other part of
        // the ModelState, not the geometry, and we should still consider the geometry value as a separate concern,
        // that we need to animate.
        return if (fromValue == targetValue) GeometryBehaviour.ANIMATE to targetValue
        // If the geometry value was added via a KEYFRAME operation mode, then the relevant value
        // will be different for the targetValue.
        // This means that the Segment was specifically created to interpolate the geometry value (probably a gesture)
        // and that it's important to follow the interpolation by snapping.
        else GeometryBehaviour.SNAP to lerpFloat(fromValue, targetValue, segmentProgress)
    }

    private enum class GeometryBehaviour {
        SNAP, ANIMATE
    }

}
