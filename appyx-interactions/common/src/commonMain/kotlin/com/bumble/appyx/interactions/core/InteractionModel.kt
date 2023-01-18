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


open class InteractionModel<NavTarget : Any, NavState : Any>(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: TransitionModel<NavTarget, NavState>,
    interpolator: Interpolator<NavTarget, NavState>,
    gestureFactory: GestureFactory<NavTarget, NavState> = GestureFactory.Noop(),
    val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val disableAnimations: Boolean = false
) : Draggable {

    companion object {
        val DefaultAnimationSpec: AnimationSpec<Float> = spring()
    }

    val frames: Flow<List<FrameModel<NavTarget, NavState>>> =
        model
            .segments
            .map { interpolator.map(it) }

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
        gestureFactory = gestureFactory
    )

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
