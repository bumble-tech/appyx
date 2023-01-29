package com.bumble.appyx.interactions.core

import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.inputsource.*
import com.bumble.appyx.interactions.core.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


open class InteractionModel<NavTarget : Any, ModelState : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: TransitionModel<NavTarget, ModelState>,
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
            if (value != field) {
                Logger.log("InteractionModel", "TransitionBounds changed: $value")
                field = value
                _interpolator = interpolator(transitionBounds)
                _gestureFactory = gestureFactory(transitionBounds)
            }
        }

    companion object {
        val DefaultAnimationSpec: AnimationSpec<Float> = spring()
    }

    val frames: Flow<List<FrameModel<NavTarget>>> =
            model
                .output
                .flatMapLatest { _interpolator.map(it) }

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

    init {
        scope.launch {
            _interpolator.isAnimating()
                .onEach {
                    if (!it) {
                        Logger.log("InteractionModel", "Finished animating, relaxing mode")
                        model.relaxExecutionMode()
                    }
                }
                .collect()
        }
    }

    override fun updateBounds(transitionBounds: TransitionBounds) {
        this.transitionBounds = transitionBounds
    }

    private val debug = DebugProgressInputSource(
        transitionModel = model,
        coroutineScope = scope
    )

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float> = defaultAnimationSpec
    ) {
        when {
            isDebug -> debug.operation(operation)
            DisableAnimations || disableAnimations -> instant.operation(operation)
            else -> animated.operation(operation, animationSpec)
        }
    }

    private fun onAnimationsFinished() {
        model.relaxExecutionMode()
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
