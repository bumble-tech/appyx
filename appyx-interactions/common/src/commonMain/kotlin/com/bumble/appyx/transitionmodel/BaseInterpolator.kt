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
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.helper.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable as Animatable1

abstract class BaseInterpolator<NavTarget : Any, ModelState, Props>(
    private val scope: CoroutineScope,
    protected val defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
) : Interpolator<NavTarget, ModelState> where Props : BaseProps, Props : HasModifier, Props : Animatable<Props> {

    private val propsCache: MutableMap<String, Props> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected var currentSpringSpec: SpringSpec<Float> = defaultAnimationSpec

    open val geometryMappings: List<Pair<(ModelState) -> Float, Animatable1<Float, AnimationVector1D>>> =
        emptyList()


    private val _finishedAnimations = MutableSharedFlow<NavElement<NavTarget>>()
    override val finishedAnimations: Flow<NavElement<NavTarget>> = _finishedAnimations

    abstract fun defaultProps(): Props

    override fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        currentSpringSpec = springSpec
    }

    final override fun isAnimating(): StateFlow<Boolean> =
        isAnimating

    abstract fun ModelState.toProps(): List<MatchedProps<NavTarget, Props>>

    override fun mapUpdate(
        update: Update<ModelState>
    ): List<FrameModel<NavTarget>> {
        val targetProps = update.currentTargetState.toProps()

        scope.launch {
            updateGeometry(update)
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = propsCache.getOrPut(t1.element.id) { defaultProps() }
            FrameModel(
                visibleState = elementProps.visibilityState,
                navElement = t1.element,
                modifier = elementProps.modifier,
                animationContainer = @Composable {
                    observeElementAnimationChanges(elementProps, t1)
                    manageAnimations(elementProps, t1, update)
                },
                progress = MutableStateFlow(1f),
            )
        }
    }

    @Composable
    private fun manageAnimations(
        elementProps: Props,
        targetProps: MatchedProps<NavTarget, Props>,
        update: Update<ModelState>
    ) {
        LaunchedEffect(update, this) {
            // make sure to use scope created by Launched effect as this scope should be cancelled
            // when associated FrameModel cease to exist
            launch {
                if (update.animate) {
                    elementProps.animateTo(
                        scope = this,
                        props = targetProps.props,
                        springSpec = currentSpringSpec,
                    )
                } else {
                    elementProps.snapTo(this, targetProps.props)
                }
            }
        }
    }

    @Composable
    private fun observeElementAnimationChanges(
        elementProps: Props,
        targetProps: MatchedProps<NavTarget, Props>
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
                                this@BaseInterpolator.javaClass.simpleName,
                                "animation for element ${targetProps.element.id} is started"
                            )
                        } else {
                            // animation finished
                            _finishedAnimations.emit(targetProps.element)
                            animations[targetProps.element.id] = false
                            isAnimating.update { animations.any { it.value } }
                            Logger.log(
                                this@BaseInterpolator.javaClass.simpleName,
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
                updatePropsVisibility()
                Logger.log(
                    this@BaseInterpolator.javaClass.simpleName,
                    "Geometry animateTo (Update) – $targetValue"
                )
            }
        }
    }

    override fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        scope.launch {
            segmentProgress.collect {
                updateGeometry(segment, it)
            }
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = propsCache.getOrPut(t1.element.id) { defaultProps() }
            //Synchronously apply current value to props before they reach composition to avoid jumping between default & current valu
            elementProps.lerpTo(scope, t0.props, t1.props, initialProgress)

            FrameModel(
                visibleState = elementProps.visibilityState,
                navElement = t1.element,
                animationContainer = @Composable {
                    interpolatedProps(segmentProgress, elementProps, t0, t1, initialProgress)
                },
                modifier = elementProps.modifier,
                progress = segmentProgress,
            )
        }
    }

    @Composable
    private fun interpolatedProps(
        segmentProgress: Flow<Float>,
        elementProps: Props,
        from: MatchedProps<NavTarget, Props>,
        to: MatchedProps<NavTarget, Props>,
        initialProgress: Float
    ) {
        val progress by segmentProgress.collectAsState(initialProgress)
        LaunchedEffect(progress) {
            elementProps.lerpTo(scope, from.props, to.props, progress)
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
                    updatePropsVisibility()
                    Logger.log(
                        this@BaseInterpolator.javaClass.simpleName,
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
                            updatePropsVisibility()
                            Logger.log(
                                this@BaseInterpolator.javaClass.simpleName,
                                "Geometry animateTo (Segment) – ${geometry.value} -> $targetValue"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updatePropsVisibility() {
        propsCache.values.forEach { it.updateVisibilityState() }
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
