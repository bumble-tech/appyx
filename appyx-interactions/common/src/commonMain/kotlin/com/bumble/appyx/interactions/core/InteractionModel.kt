package com.bumble.appyx.interactions.core

import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.DragProgressInputSource
import com.bumble.appyx.interactions.core.inputsource.Draggable
import com.bumble.appyx.interactions.core.inputsource.InstantInputSource
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.FlexibleBounds
import com.bumble.appyx.interactions.core.ui.TransitionBounds


open class InteractionModel<NavTarget : Any, NavState : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TransitionModel<NavTarget, NavState>,
    private val interpolator: (TransitionBounds) -> Interpolator<NavTarget, NavState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, NavState> = { GestureFactory.Noop() },
    val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val disableAnimations: Boolean = false
) : Draggable, FlexibleBounds {

    private var _interpolator: Interpolator<NavTarget, NavState> = interpolator(TransitionBounds(Density(0f), 0, 0))
    private var _gestureFactory: GestureFactory<NavTarget, NavState> = gestureFactory(TransitionBounds(Density(0f), 0, 0))
    private var transitionBounds: TransitionBounds = TransitionBounds(Density(0f), 0, 0)
        set(value) {
            field = value
            _interpolator = interpolator(transitionBounds)
            _gestureFactory = gestureFactory(transitionBounds)
        }

    companion object {
        val DefaultAnimationSpec: AnimationSpec<Float> = spring()
    }

    val frames: Flow<List<FrameModel<NavTarget, NavState>>> =
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
        gestureFactory = _gestureFactory
    )

    override fun updateBounds(transitionBounds: TransitionBounds) {
        this.transitionBounds = transitionBounds
    }

    fun operation(
        operation: Operation<NavTarget, NavState>,
        animationSpec: AnimationSpec<Float> = defaultAnimationSpec
    ) {
        if (DisableAnimations || disableAnimations) {
            instant.operation(operation)
        } else {
            animated.operation(operation, animationSpec)
        }
    }

    override fun onDrag(dragAmount: Offset, density: Density) {
        drag.onDrag(dragAmount, density)
    }

    override fun onDragEnd() {
        drag.onDragEnd()
        settle()
    }

    fun settle(
        // FIXME @FloatRange(from = 0.0, to = 1.0)
        roundUpThreshold: Float = 0.5f,
        roundUpAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
        roundDownAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    ) {
        animated.settle(roundUpThreshold, roundUpAnimationSpec, roundDownAnimationSpec)
    }

    fun settleDefault() {
        settle()
    }
}
