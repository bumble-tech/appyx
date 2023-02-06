package com.bumble.appyx.interactions.core

import DefaultAnimationSpec
import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.DebugProgressInputSource
import com.bumble.appyx.interactions.core.inputsource.DragProgressInputSource
import com.bumble.appyx.interactions.core.inputsource.Draggable
import com.bumble.appyx.interactions.core.inputsource.InstantInputSource
import com.bumble.appyx.interactions.core.ui.FlexibleBounds
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.ScreenState
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.toScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


// TODO move to navigation
// TODO save/restore state
open class InteractionModel<NavTarget : Any, ModelState : Any>(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: TransitionModel<NavTarget, ModelState>,
    private val interpolator: (TransitionBounds) -> Interpolator<NavTarget, ModelState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, ModelState> = { GestureFactory.Noop() },
    val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val animateSettle: Boolean = false,
    private val disableAnimations: Boolean = false,
    private val isDebug: Boolean = false
) : Draggable, FlexibleBounds {

    private var animationScope: CoroutineScope? = null
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

    private var isAnimating: Boolean = false
    private val instant = InstantInputSource(model = model)
    private var animated: AnimatedInputSource<NavTarget, ModelState>? = null
    private var debug: DebugProgressInputSource<NavTarget, ModelState>? = null
    private val drag = DragProgressInputSource(
        model = model,
        gestureFactory = { _gestureFactory }
    )

    val frames: Flow<List<FrameModel<NavTarget>>> =
        model
            .output
            .map { _interpolator.map(it) }

    val screenState: Flow<ScreenState<NavTarget>> =
        frames.map { it.toScreenState() }

    init {
        scope.launch {
            _interpolator.isAnimating()
                .onEach {
                    if (!it) {
                        Logger.log("InteractionModel", "Finished animating")
                        onAnimationsFinished()
                    } else {
                        onAnimationsStarted()
                    }
                }
                .collect()
        }
    }

    fun onAddedToComposition(scope: CoroutineScope) {
        animationScope = scope
        createAnimatedInputSource(scope)
        createdDebugInputSource(scope)
    }

    fun onRemovedFromComposition() {
        // TODO finish unfinished transitions
        if (isDebug) debug?.stopModel() else animated?.stopModel()
        animationScope?.cancel()
    }

    private fun createAnimatedInputSource(scope: CoroutineScope) {
        animated = AnimatedInputSource(
            model = model,
            coroutineScope = scope,
            defaultAnimationSpec = defaultAnimationSpec,
            animateSettle = animateSettle
        )
    }

    private fun createdDebugInputSource(scope: CoroutineScope) {
        debug = DebugProgressInputSource(
            transitionModel = model,
            coroutineScope = scope
        )
    }

    override fun updateBounds(transitionBounds: TransitionBounds) {
        this.transitionBounds = transitionBounds
    }

    fun availableElements(): Set<NavElement<NavTarget>> = model.availableElements()

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float> = defaultAnimationSpec
    ) {
       if (operation.mode == IMMEDIATE && animationSpec is SpringSpec<Float>) _interpolator.overrideAnimationSpec(animationSpec)
        val animatedSource = animated
        val debugSource = debug
        when {
            (isDebug && debugSource != null) -> debugSource.operation(operation)
            animatedSource == null || DisableAnimations || disableAnimations -> instant.operation(operation)
            else -> animatedSource.operation(operation, animationSpec)
        }
    }

    private fun onAnimationsStarted() {
        isAnimating = true
    }

    private fun onAnimationsFinished() {
        isAnimating = false
        model.relaxExecutionMode()
    }

    override fun onStartDrag(position: Offset) {
        drag.onStartDrag(position)
    }

    override fun onDrag(dragAmount: Offset, density: Density) {
        if (!isAnimating) {
            drag.onDrag(dragAmount, density)
        }
    }

    override fun onDragEnd(
        completionThreshold: Float,
        completeGestureSpec: AnimationSpec<Float>,
        revertGestureSpec: AnimationSpec<Float>
    ) {
        if (!isAnimating) {
            drag.onDragEnd()
            settle(completionThreshold, revertGestureSpec, completeGestureSpec)
        }
    }

    private fun settle(
        completionThreshold: Float = 0.5f,
        completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
        revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    ) {
        if (isDebug) {
            debug?.settle()
        } else {
            animated?.settle(completionThreshold, completeGestureSpec, revertGestureSpec)
        }
    }

    // TODO plugin?!
    fun destroy() {
        scope.cancel()
    }

    fun setNormalisedProgress(progress: Float) {
        debug?.setNormalisedProgress(progress)
    }
}
