package com.bumble.appyx.interactions.core

import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.Operation.Mode
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.DebugProgressInputSource
import com.bumble.appyx.interactions.core.inputsource.DragProgressInputSource
import com.bumble.appyx.interactions.core.inputsource.Draggable
import com.bumble.appyx.interactions.core.inputsource.InstantInputSource
import com.bumble.appyx.interactions.core.ui.FlexibleBounds
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


open class InteractionModel<NavTarget : Any, ModelState : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TransitionModel<NavTarget, ModelState>,
    private val interpolator: (TransitionBounds) -> Interpolator<NavTarget, ModelState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, ModelState> = { GestureFactory.Noop() },
    val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val disableAnimations: Boolean = false,
    private val isDebug: Boolean = false
) : Draggable, FlexibleBounds {

    private var _interpolator: Interpolator<NavTarget, ModelState> =
        interpolator(TransitionBounds(Density(0f), 0, 0))
    private var _gestureFactory: GestureFactory<NavTarget, ModelState> =
        gestureFactory(TransitionBounds(Density(0f), 0, 0))
    private var transitionBounds: TransitionBounds = TransitionBounds(Density(0f), 0, 0)
        set(value) {
            field = value
            _interpolator = interpolator(transitionBounds)
            _gestureFactory = gestureFactory(transitionBounds)
        }

    companion object {
        val DefaultAnimationSpec: AnimationSpec<Float> = spring()
    }

    val frames: Flow<List<FrameModel<NavTarget>>> =
        model
            .segments
            .map { _interpolator.map(it) }

    private val instant = InstantInputSource(
        model = model
    )

    private val animated = AnimatedInputSource(
        model = model,
        coroutineScope = scope,
        defaultAnimationSpec = defaultAnimationSpec
    )

    private val drag = DragProgressInputSource(
        model = model,
        gestureFactory = { _gestureFactory }
    )

    override fun updateBounds(transitionBounds: TransitionBounds) {
        this.transitionBounds = transitionBounds
    }

    private val debug = DebugProgressInputSource(
        navModel = model,
        coroutineScope = scope
    )

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
        mode: Mode = KEYFRAME,
    ) {
        when {
            isDebug -> debug.operation(operation)
            DisableAnimations || disableAnimations -> instant.operation(operation)
            else -> animated.operation(operation, mode, animationSpec)
        }
    }

    override fun onStartDrag(position: Offset) {
        drag.onStartDrag(position)
    }

    override fun onDrag(dragAmount: Offset, density: Density) {
        drag.onDrag(dragAmount, density)
    }

    override fun onDragEnd(
        completionThreshold: Float,
        completeGestureSpec: AnimationSpec<Float>,
        revertGestureSpec: AnimationSpec<Float>
    ) {
        drag.onDragEnd()
        settle(completionThreshold, revertGestureSpec, completeGestureSpec)
    }

    private fun settle(
        completionThreshold: Float = 0.5f,
        completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
        revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    ) {
        if (isDebug) {
            debug.settle()
        } else {
            animated.settle(completionThreshold, completeGestureSpec, revertGestureSpec)
        }

    }

    fun settleDefault() {
        settle()
    }

    fun setNormalisedProgress(progress: Float) {
        debug.setNormalisedProgress(progress)
    }
}
