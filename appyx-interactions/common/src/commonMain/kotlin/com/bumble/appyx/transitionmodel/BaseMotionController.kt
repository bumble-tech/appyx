package com.bumble.appyx.transitionmodel

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.TargetUiStateResolver.Companion.infer
import com.bumble.appyx.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias FieldOfStateType<ModelState> = (ModelState) -> Float
typealias ViewpointType<ModelState, KeyframeSteps> = Triple<FieldOfStateType<ModelState>, GenericFloatProperty, KeyframeSteps?>

abstract class BaseMotionController<InteractionTarget : Any, ModelState, MutableUiState, TargetUiState>(
    private val uiContext: UiContext,
    protected val defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
) : MotionController<InteractionTarget, ModelState> where MutableUiState : BaseMutableUiState<TargetUiState> {

    open val viewpointDimensions: List<ViewpointType<ModelState, KeyframeSteps<TargetUiState>?>> =
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

    private var currentSpringSpec: SpringSpec<Float> = defaultAnimationSpec

    private val _finishedAnimations = MutableSharedFlow<Element<InteractionTarget>>()
    override val finishedAnimations: Flow<Element<InteractionTarget>> = _finishedAnimations

    private lateinit var targetUiStateResolver: TargetUiStateResolver<TargetUiState, MutableUiState>

    override fun onCreated() {
        // Infer which TargetUiStateResolver should be used:
        //  * if all viewpointDimensions are keyframe-based -> apply dimension value to guess the two TargetUiState
        //    needed to interpolate that dimension. Also consider how should we interpolate between different
        //    dimensions when many are used.
        //  * if none viewpointDimensions is keyframe-based -> business as usual
        //  * otherwise we have a mix of them -> throw exception
        targetUiStateResolver = viewpointDimensions.infer()
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

    open suspend fun MutableUiState.mutableAnimateTo(scope: CoroutineScope, targetUiState: TargetUiState, springSpec: SpringSpec<Float>) {
        animateTo(
            scope = scope,
            target = targetUiState,
            springSpec = currentSpringSpec,
        ).also {
            AppyxLogger.d("mutableUiState", "animateTo")
        }
    }


    override fun mapUpdate(
        update: Update<ModelState>
    ): List<ElementUiModel<InteractionTarget>> {
        AppyxLogger.d("BaseMotionController", "mapUpdate -> toUiTargets [${update.currentTargetState} = ${update.lastTargetState}]")
        val matchedTargetUiStates = update.currentTargetState.toUiTargets()

        cleanUpCacheForDestroyedElements(matchedTargetUiStates)

        coroutineScope.launch {
            updateViewpoint(update)
        }

        // TODO: use a map instead of find
        return matchedTargetUiStates.map { t1 ->
            val mutableUiState = mutableUiStateCache.getOrPut(t1.element.id) {
                mutableUiStateFor(uiContext, t1.targetUiState)
            }
            ElementUiModel(
                element = t1.element,
                visibleState = mutableUiState.isVisible,
                persistentContainer = @Composable {
                    Box(modifier = mutableUiState.visibilityModifier)
                    observeElementAnimationChanges(mutableUiState, t1)
                    manageAnimations(mutableUiState, t1, update)
                },
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
    private fun manageAnimations(
        mutableUiState: MutableUiState,
        matchedTargetUiState: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        update: Update<ModelState>
    ) {
        LaunchedEffect(update, this) {
            // Make sure to use the scope created by LaunchedEffect as this scope should be cancelled
            // when the associated ElementUiModel ceases to exist
            launch {
                if (update.animate) {
                    mutableUiState.mutableAnimateTo(
                        scope = this,
                        targetUiState = matchedTargetUiState.targetUiState,
                        springSpec = currentSpringSpec,
                    ).also {
                        AppyxLogger.d("mutableUiState", "animateTo")
                    }
                } else {
                    mutableUiState.snapTo(matchedTargetUiState.targetUiState)
                    AppyxLogger.d("mutableUiState", "snapTo")
                }
            }
        }
    }

    @Composable
    private fun observeElementAnimationChanges(
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

    private suspend fun updateViewpoint(update: Update<ModelState>) {
        viewpointDimensions.forEach { (fieldOfState, viewpointDimension) ->
            val targetValue = fieldOfState(update.currentTargetState)
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

    override fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<ElementUiModel<InteractionTarget>> {
        val (fromState, targetState) = segment.stateTransition
        AppyxLogger.d("BaseMotionController", "mapUpdate -> toUiTargets")
        val fromTargetUiState = fromState.toUiTargets()
        val toTargetUiState = targetState.toUiTargets()

        cleanUpCacheForDestroyedElements(toTargetUiState)

        coroutineScope.launch {
            segmentProgress.collect {
                updateViewpoint(segment, it)
            }
        }

        // TODO: use a map instead of find
        return toTargetUiState.map { t1 ->
            val t0 = fromTargetUiState.find { it.element.id == t1.element.id }!!
            val mutableUiState = mutableUiStateCache.getOrPut(t1.element.id) {
                mutableUiStateFor(uiContext, t0.targetUiState)
            }
            // Synchronously, immediately apply current interpolated value before the new mutable state
            // reaches composition. This is to avoid jumping between default & current value.
            val lerpInfo = targetUiStateResolver.resolveLerpInfo(t0.targetUiState, t1.targetUiState, initialProgress)
            mutableUiState.lerpTo(coroutineScope, lerpInfo.from, lerpInfo.to, lerpInfo.fraction)

            ElementUiModel(
                element = t1.element,
                visibleState = mutableUiState.isVisible,
                persistentContainer = @Composable {
                    Box(modifier = mutableUiState.visibilityModifier)
                    interpolateUiState(segmentProgress, mutableUiState, t0, t1, initialProgress)
                },
                modifier = mutableUiState.modifier,
                progress = segmentProgress,
            )
        }
    }

    @Composable
    private fun interpolateUiState(
        segmentProgress: Flow<Float>,
        mutableUiState: MutableUiState,
        from: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        to: MatchedTargetUiState<InteractionTarget, TargetUiState>,
        initialProgress: Float
    ) {
        val progress by segmentProgress.collectAsState(initialProgress)
        LaunchedEffect(progress) {
            val lerpInfo = targetUiStateResolver.resolveLerpInfo(from.targetUiState, to.targetUiState, progress)
            mutableUiState.lerpTo(coroutineScope, lerpInfo.from, lerpInfo.to, lerpInfo.fraction)
            AppyxLogger.d("mutableUiState", "lerpTo (interpolateUiState) $progress")
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
//                    AppyxLogger.d(TAG, "Viewpoint snapTo (Segment): $targetValue")
                }

                ViewpointBehaviour.ANIMATE -> {
                    if (viewpointDimension.internalValue != targetValue) {
                        viewpointDimension.animateTo(
                            targetValue, spring(
                                stiffness = currentSpringSpec.stiffness,
                                dampingRatio = currentSpringSpec.dampingRatio
                            )
                        ) {
//                            AppyxLogger.d(TAG, "Viewpoint animateTo (Segment) – ${viewpointDimension.internalValue} -> $targetValue")
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

infix fun <ModelState> FieldOfStateType<ModelState>.mapTo(property: GenericFloatProperty): ViewpointType<ModelState, Nothing?> =
    Triple(this, property, null)

infix fun <ModelState, KeyframeSteps> Triple<FieldOfStateType<ModelState>, GenericFloatProperty, Nothing?>.keyframeWith(axisKeyframes: KeyframeSteps): ViewpointType<ModelState, KeyframeSteps> =
    Triple(first, second, axisKeyframes)
