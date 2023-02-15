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
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable as Animatable1

abstract class BaseInterpolator<NavTarget : Any, ModelState, Props>(
    private val scope: CoroutineScope,
    protected val defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
) : Interpolator<NavTarget, ModelState> where Props : BaseProps, Props : HasModifier, Props : Animatable<Props> {

    private val cache: MutableMap<String, Props> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected var currentSpringSpec: SpringSpec<Float> = defaultAnimationSpec

    open val geometryMappings: List<Pair<(ModelState) -> Float, Animatable1<Float, AnimationVector1D>>> =
        emptyList()


    abstract fun defaultProps(): Props

    override fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        currentSpringSpec = springSpec
    }

    final override fun isAnimating(): StateFlow<Boolean> =
        isAnimating

    fun updateAnimationState(key: String, isAnimating: Boolean) {
        animations[key] = isAnimating
        this.isAnimating.update { isAnimating || animations.any { it.value } }
    }

    abstract fun ModelState.toProps(): List<MatchedProps<NavTarget, Props>>

    override fun mapUpdate(update: Update<ModelState>): List<FrameModel<NavTarget>> {
        val targetProps = update.currentTargetState.toProps()

        scope.launch {
            updateGeometry(update)
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id) { defaultProps() }
            FrameModel(
                visibleState = elementProps.visibilityState,
                navElement = t1.element,
                modifier = elementProps.modifier,
                animationContainer = @Composable {
                    LaunchedEffect(update) {
                        scope.launch {
                            if (update.animate) {
                                elementProps.animateTo(
                                    scope = this,
                                    props = t1.props,
                                    springSpec = currentSpringSpec,
                                    onStart = {
                                        updateAnimationState(t1.element.id, true)
                                    },
                                    onFinished = {
                                        updateAnimationState(t1.element.id, false)
                                        currentSpringSpec = defaultAnimationSpec
                                    },
                                )
                            } else {
                                elementProps.snapTo(this, t1.props)
                            }
                        }
                    }
                },
                progress = MutableStateFlow(1f),
            )
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
        segmentProgress: StateFlow<Float>
    ): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        scope.launch {
            segmentProgress.collect {
                updateGeometry(segment, segmentProgress.value)
            }
        }

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id) { defaultProps() }
            //Synchronously apply current value to props before they reach composition to avoid jumping between default & current valu
            elementProps.lerpTo(scope, t0.props, t1.props, segmentProgress.value)

            FrameModel(
                visibleState = elementProps.visibilityState,
                navElement = t1.element,
                animationContainer = @Composable {
                    interpolatedProps(segmentProgress, elementProps, t0, t1)
                },
                modifier = elementProps.modifier,
                progress = segmentProgress,
            )
        }
    }

    @Composable
    private fun interpolatedProps(
        segmentProgress: StateFlow<Float>,
        elementProps: Props,
        from: MatchedProps<NavTarget, Props>,
        to: MatchedProps<NavTarget, Props>
    ) {
        val progress by segmentProgress.collectAsState(segmentProgress.value)
        LaunchedEffect(progress) {
            elementProps.lerpTo(scope, from.props, to.props, progress)
        }
    }

    private suspend fun updateGeometry(
        segment: Segment<ModelState>,
        segmentProgress: Float
    ) {
        geometryMappings.forEach { (fieldOfState, geometry) ->
            val (behaviour, targetValue) = geometryTargetValue(segment, segmentProgress, fieldOfState)

            when (behaviour) {
                GeometryBehaviour.SNAP -> {
                    geometry.snapTo(targetValue)
                    updatePropsVisibility()
                    Logger.log(this@BaseInterpolator.javaClass.simpleName, "Geometry snapTo (Segment): $targetValue")
                }

                GeometryBehaviour.ANIMATE -> {
                    if (geometry.value != targetValue) {
                        geometry.animateTo(targetValue, spring(
                            stiffness = currentSpringSpec.stiffness,
                            dampingRatio = currentSpringSpec.dampingRatio
                        )) {
                            updatePropsVisibility()
                            Logger.log(this@BaseInterpolator.javaClass.simpleName, "Geometry animateTo (Segment) – ${geometry.value} -> $targetValue")
                        }
                    }
                }
            }
        }
    }

    private fun updatePropsVisibility() {
        cache.values.forEach { it.updateVisibilityState() }
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
        else GeometryBehaviour.SNAP to Interpolator.lerpFloat(fromValue, targetValue, segmentProgress)
    }

    private enum class GeometryBehaviour {
        SNAP, ANIMATE
    }

}
