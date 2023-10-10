package com.bumble.appyx.transitionmodel

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.utils.multiplatform.AppyxLogger
import com.bumble.appyx.withPrevious
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
abstract class BaseVisualisation<InteractionTarget : Any, ModelState, MutableUiState, TargetUiState>(
    private val uiContext: UiContext,
    protected val defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
) : Visualisation<InteractionTarget, ModelState> where MutableUiState : BaseMutableUiState<TargetUiState> {

    open val viewpointDimensions: List<Pair<(ModelState) -> Float, GenericFloatProperty>> =
        emptyList()

    private val coroutineScope = uiContext.coroutineScope
    private val mutableUiStateCache: MutableMap<String, MutableUiState> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val viewpointIsAnimating: StateFlow<Boolean>
        get() = if (viewpointDimensions.isNotEmpty()) {
            combineState(
                viewpointDimensions.map { it.second.isAnimating },
                uiContext.coroutineScope
            ) { values ->
                values.any { it }
            }
        } else {
            MutableStateFlow(false)
        }
    private val updateIsAnimating = MutableStateFlow(false)
    private val isAnimatingState: StateFlow<Boolean>
        get() = updateIsAnimating.combineState(
            viewpointIsAnimating,
            uiContext.coroutineScope
        ) { b1, b2 ->
            b1 || b2
        }

    protected var currentSpringSpec: SpringSpec<Float> = defaultAnimationSpec

    private val _finishedAnimations = MutableSharedFlow<Element<InteractionTarget>>()
    override val finishedAnimations: Flow<Element<InteractionTarget>> = _finishedAnimations

    protected var transitionBounds: TransitionBounds = TransitionBounds.Zero

    override fun updateBounds(transitionBounds: TransitionBounds) {
        this.transitionBounds = transitionBounds
        mutableUiStateCache.values.forEach {
            it.updateBounds(transitionBounds)
        }
    }

    override fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        currentSpringSpec = springSpec
    }

    final override fun isAnimating(): StateFlow<Boolean> =
        isAnimatingState

    abstract fun ModelState.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>>

    abstract fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState

    override fun mapUpdate(
        update: Update<ModelState>
    ): List<ElementUiModel<InteractionTarget>> {
        val matchedTargetUiStates = update.currentTargetState.toUiTargets()

        cleanUpCacheForDestroyedElements(matchedTargetUiStates)

        val isRecreating = mutableUiStateCache.isEmpty()
        coroutineScope.launch {
            updateViewpoint(update, isRecreating)
        }


        @Suppress("ForbiddenComment")
        // TODO: use a map instead of find
        return matchedTargetUiStates.map { t1 ->
            val mutableUiState = mutableUiStateCache.getOrPut(t1.element.id) {
                mutableUiStateFor(uiContext, t1.targetUiState).apply {
                    updateBounds(transitionBounds)
                }
            }
            ElementUiModel(
                element = t1.element,
                visibleState = mutableUiState.isVisible,
                persistentContainer = @Composable {
                    Box(modifier = mutableUiState.visibilityModifier)
                    ObserveElementAnimationChanges(mutableUiState, t1)
                    ManageAnimations(mutableUiState, t1, update)
                },
                motionProperties = mutableUiState.motionProperties,
                modifier = mutableUiState.modifier,
                progress = MutableStateFlow(1f),
            )
        }
    }

    private fun cleanUpCacheForDestroyedElements(
        matchedTargetUiStates: List<MatchedTargetUiState<InteractionTarget, TargetUiState>>
    ) {
        val availableIds = matchedTargetUiStates.map { it.element.id }
        val iterator = mutableUiStateCache.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (!availableIds.contains(key)) {
                iterator.remove()
            }
        }
    }

    @Composable
    private fun ManageAnimations(
        mutableUiState: MutableUiState,
        matchedTargetUiState: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        update: Update<ModelState>
    ) {
        LaunchedEffect(update, this) {
            // Make sure to use the scope created by LaunchedEffect as this scope should be cancelled
            // when the associated ElementUiModel ceases to exist
            launch {
                if (update.animate) {
                    mutableUiState.animateTo(
                        scope = this,
                        target = matchedTargetUiState.targetUiState,
                        springSpec = currentSpringSpec,
                    )
                } else {
                    mutableUiState.snapTo(matchedTargetUiState.targetUiState)
                }
            }
        }
    }

    @Composable
    private fun ObserveElementAnimationChanges(
        mutableUiState: MutableUiState,
        matchedTargetUiState: MatchedTargetUiState<InteractionTarget, TargetUiState>
    ) {
        LaunchedEffect(this) {
            // Make sure to use the scope created by LaunchedEffect as this scope should be cancelled
            // when the associated ElementUiModel ceases to exist
            launch {
                mutableUiState.isAnimating
                    .distinctUntilChanged()
                    .withPrevious()
                    .collect { values ->
                        val previous = values.previous ?: return@collect
                        val current = values.current
                        if (current && !previous) {
                            // animation started
                            animations[matchedTargetUiState.element.id] = true
                            updateIsAnimating.update { true }
                            AppyxLogger.d(TAG, "animation for element ${matchedTargetUiState.element.id} is started")
                        } else {
                            // animation finished
                            _finishedAnimations.emit(matchedTargetUiState.element)
                            animations[matchedTargetUiState.element.id] = false
                            updateIsAnimating.update { animations.any { it.value } }
                            AppyxLogger.d(TAG, "animation for element ${matchedTargetUiState.element.id} is finished")
                        }
                    }
            }
        }
    }

    private suspend fun updateViewpoint(
        update: Update<ModelState>,
        isRecreating: Boolean
    ) {
        viewpointDimensions.forEach { (fieldOfState, viewpointDimension) ->
            val targetValue = fieldOfState(update.currentTargetState)
            if (isRecreating) {
                viewpointDimension.snapTo(targetValue)
            } else {
                viewpointDimension.animateTo(
                    targetValue,
                    spring(
                        stiffness = currentSpringSpec.stiffness,
                        dampingRatio = currentSpringSpec.dampingRatio
                    )
                ) {
                    AppyxLogger.d(TAG, "Viewpoint animateTo (Update) – $targetValue")
                }
            }
        }
    }

    override fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<ElementUiModel<InteractionTarget>> {
        val (fromState, targetState) = segment.stateTransition
        val fromTargetUiState = fromState.toUiTargets()
        val toTargetUiState = targetState.toUiTargets()

        cleanUpCacheForDestroyedElements(toTargetUiState)

        coroutineScope.launch {
            segmentProgress.collect {
                updateViewpoint(segment, it)
            }
        }

        @Suppress("ForbiddenComment")
        // TODO: use a map instead of find
        return toTargetUiState.map { t1 ->
            val t0 = fromTargetUiState.find { it.element.id == t1.element.id }!!
            val mutableUiState = mutableUiStateCache.getOrPut(t1.element.id) {
                mutableUiStateFor(uiContext, t0.targetUiState).apply {
                    updateBounds(transitionBounds)
                }
            }
            // Synchronously, immediately apply current interpolated value before the new mutable state
            // reaches composition. This is to avoid jumping between default & current value.
            mutableUiState.lerpTo(coroutineScope, t0.targetUiState, t1.targetUiState, initialProgress)

            ElementUiModel(
                element = t1.element,
                visibleState = mutableUiState.isVisible,
                persistentContainer = @Composable {
                    Box(modifier = mutableUiState.visibilityModifier)
                    InterpolateUiState(segmentProgress, mutableUiState, t0, t1, initialProgress)
                },
                motionProperties = mutableUiState.motionProperties,
                modifier = mutableUiState.modifier,
                progress = segmentProgress,
            )
        }
    }

    @Composable
    private fun InterpolateUiState(
        segmentProgress: Flow<Float>,
        mutableUiState: MutableUiState,
        from: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        to: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        initialProgress: Float
    ) {
        val progress by segmentProgress.collectAsState(initialProgress)
        LaunchedEffect(progress) {
            mutableUiState.lerpTo(coroutineScope, from.targetUiState, to.targetUiState, progress)
        }
    }

    private suspend fun updateViewpoint(
        segment: Segment<ModelState>,
        segmentProgress: Float
    ) {
        viewpointDimensions.forEach { (fieldOfState, viewpointDimension) ->
            val (behaviour, targetValue) = viewpointTargetValue(
                segment,
                segmentProgress,
                fieldOfState
            )

            when (behaviour) {
                ViewpointBehaviour.SNAP -> {
                    viewpointDimension.snapTo(targetValue)
                    AppyxLogger.d(TAG, "Viewpoint snapTo (Segment): $targetValue")
                }

                ViewpointBehaviour.ANIMATE -> {
                    if (viewpointDimension.internalValue != targetValue) {
                        viewpointDimension.animateTo(
                            targetValue, spring(
                                stiffness = currentSpringSpec.stiffness,
                                dampingRatio = currentSpringSpec.dampingRatio
                            )
                        ) {
                            AppyxLogger.d(TAG,
                                "Viewpoint animateTo (Segment) – ${viewpointDimension.internalValue} -> $targetValue")
                        }
                    }
                }
            }
        }
    }

    private fun viewpointTargetValue(
        segment: Segment<ModelState>,
        segmentProgress: Float,
        fieldOfState: (ModelState) -> Float
    ): Pair<ViewpointBehaviour, Float> {
        val fromValue = fieldOfState(segment.fromState)
        val targetValue = fieldOfState(segment.targetState)

        // If the viewpoint value was updated via a IMPOSED operation mode, then it was applied both to the
        // start and end values (see [BaseTransitionModel.impose()], and they should be the same.
        // This means that even though we have a Segment, the interpolation is working on some other part of
        // the ModelState, not the viewpoint, and we should still consider the viewpoint value as a separate concern,
        // that we need to animate.
        return if (fromValue == targetValue) ViewpointBehaviour.ANIMATE to targetValue
        // If the viewpoint target value was updated via a KEYFRAME operation mode, then the relevant value
        // will be different for the targetValue.
        // This means that the Segment was specifically created to interpolate the viewpoint value (probably a gesture)
        // and that it's important to follow the interpolation by snapping.
        else ViewpointBehaviour.SNAP to lerpFloat(fromValue, targetValue, segmentProgress)
    }

    private enum class ViewpointBehaviour {
        SNAP, ANIMATE
    }

    private companion object {
        const val TAG = "BaseMotionController"
    }

}
