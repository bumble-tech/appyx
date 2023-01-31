package com.bumble.appyx.transitionmodel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.composed
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

abstract class BaseInterpolator<NavTarget : Any, ModelState, Props>(private val defaultProps: () -> Props) :
    Interpolator<NavTarget, ModelState> where Props : BaseProps, Props : HasModifier, Props : Interpolatable<Props>, Props : Animatable<Props> {

    private val cache: MutableMap<String, Props> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override fun isAnimating(): StateFlow<Boolean> =
        isAnimating

    fun updateAnimationState(key: String, isAnimating: Boolean) {
        animations[key] = isAnimating
        this.isAnimating.update { isAnimating || animations.any { it.value } }
    }

    abstract fun ModelState.toProps(): List<MatchedProps<NavTarget, Props>>

    override fun mapUpdate(update: Update<ModelState>): List<FrameModel<NavTarget>> {
        val targetProps = update.currentTargetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id, defaultProps)

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier.composed {
                    LaunchedEffect(update) {
                        elementProps.animateTo(
                            scope = this,
                            props = t1.props,
                            onStart = { updateAnimationState(t1.element.id, true) },
                            onFinished = { updateAnimationState(t1.element.id, false) },
                        )
                    }
                    this
                },
                progress = 1f,
            )
        }
    }

    override fun mapSegment(segment: Segment<ModelState>, segmentProgress: Float): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id, defaultProps)
            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segmentProgress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier.composed { this },
                progress = segmentProgress,
                state = resolveNavElementVisibility(t0.props, t1.props)
            )
        }
    }
}
