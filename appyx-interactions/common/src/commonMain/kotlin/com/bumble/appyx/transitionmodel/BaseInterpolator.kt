package com.bumble.appyx.transitionmodel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.composed
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import kotlinx.coroutines.runBlocking

abstract class BaseInterpolator<NavTarget : Any, ModelState, Props>(private val defaultProps: () -> Props) :
    Interpolator<NavTarget, ModelState> where Props : BaseProps, Props : HasModifier, Props : Interpolatable<Props>, Props : Animatable<Props> {

    private val cache: MutableMap<String, Props> = mutableMapOf()

    abstract fun ModelState.toProps(): List<MatchedProps<NavTarget, Props>>

    override fun mapUpdate(update: TransitionModel.Output.Update<ModelState>): List<FrameModel<NavTarget>> {
        val targetProps = update.targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id, defaultProps)

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier.composed {
                        LaunchedEffect(update) {
                            elementProps.animateTo(this, t1.props)
                        }
                        this
                    },
                progress = 1f,
            )
        }
    }

    override fun mapSegment(segment: TransitionModel.Output.Segment<ModelState>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id, defaultProps)
            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segment.progress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier.composed { this },
                progress = segment.progress,
                state = resolveNavElementVisibility(t0.props, t1.props)
            )
        }
    }
}