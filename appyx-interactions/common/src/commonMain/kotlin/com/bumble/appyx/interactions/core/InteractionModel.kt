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
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.ScreenState
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.interactions.core.ui.UiContextAware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


// TODO move to navigation
// TODO save/restore state
open class InteractionModel<NavTarget : Any, ModelState : Any>(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: TransitionModel<NavTarget, ModelState>,
    private val interpolator: (UiContext) -> Interpolator<NavTarget, ModelState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<NavTarget, ModelState> = { GestureFactory.Noop() },
    val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val animateSettle: Boolean = false,
    private val disableAnimations: Boolean = false,
    private val isDebug: Boolean = false
) : Draggable, UiContextAware {

    private var interpolatorObserverJob: Job? = null
    private var _interpolator: Interpolator<NavTarget, ModelState>? = null

    private var _gestureFactory: GestureFactory<NavTarget, ModelState> =
        gestureFactory(TransitionBounds(Density(0f), 0, 0))

    private var animationChangesJob: Job? = null

    private var transitionBounds: TransitionBounds = TransitionBounds(Density(0f), 0, 0)
        set(value) {
            if (value != field) {
                Logger.log("InteractionModel", "TransitionBounds changed: $value")
                field = value
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

    private val _frames: MutableStateFlow<List<FrameModel<NavTarget>>> =
        MutableStateFlow(emptyList())
    val frames: StateFlow<List<FrameModel<NavTarget>>> = _frames

    private var screenStateJob: Job
    private val _screenState: MutableStateFlow<ScreenState<NavTarget>> =
        MutableStateFlow(ScreenState(offScreen = model.availableElements().value))
    val screenState: StateFlow<ScreenState<NavTarget>> = _screenState


    init {
        // before interpolator is ready we consider all nav elements as off screen
        screenStateJob = scope.launch {
            model
                .availableElements()
                .collect {
                    _screenState.emit(ScreenState(offScreen = it))
                }
        }
    }

    private var animationScope: CoroutineScope? = null
    private var isInitialised: Boolean = false

    private fun observeAnimationChanges(interpolator: Interpolator<NavTarget, ModelState>) {
        animationChangesJob?.cancel()
        animationChangesJob = scope.launch {
            interpolator.isAnimating()
                .collect {
                    if (!it) {
                        Logger.log("InteractionModel", "Finished animating")
                        onAnimationsFinished()
                    } else {
                        onAnimationsStarted()
                    }
                }
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

    override fun updateContext(uiContext: UiContext) {
        if (this.transitionBounds != uiContext.transitionBounds) {
            this.transitionBounds = uiContext.transitionBounds
            _interpolator = interpolator(uiContext).also {
                onInterpolatorReady(it)
            }
            _gestureFactory = gestureFactory(transitionBounds)
        }
    }

    private fun onInterpolatorReady(interpolator: Interpolator<NavTarget, ModelState>) {
        observeAnimationChanges(interpolator)
        observeInterpolator(interpolator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeInterpolator(interpolator: Interpolator<NavTarget, ModelState>) {
        screenStateJob.cancel()
        interpolatorObserverJob?.cancel()
        interpolatorObserverJob = scope.launch {
            model
                .output
                .flatMapLatest { interpolator.mapCore(it) }
                .flatMapLatest { frames ->
                    val frameVisibilityFlows = frames.map { frame ->
                        frame.visibleState
                    }
                    combine(frameVisibilityFlows) { visibilityValues ->
                        val onScreen = mutableSetOf<NavElement<NavTarget>>()
                        val offScreen = mutableSetOf<NavElement<NavTarget>>()
                        visibilityValues.forEachIndexed { index, visibilityValue ->
                            val navElement = frames[index].navElement
                            if (visibilityValue) {
                                onScreen.add(navElement)
                            } else {
                                offScreen.add(navElement)
                            }
                        }
                        ScreenState(onScreen = onScreen, offScreen = offScreen) to frames
                    }
                }
                .collect { (screenState, frames) ->
                    // order is important here. We need to report screen state to the ParentNode first
                    // before frames are consumed by the UI
                    _screenState.emit(screenState)
                    _frames.emit(frames)
                }
        }
    }

    fun availableElements(): StateFlow<Set<NavElement<NavTarget>>> = model.availableElements()

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float> = defaultAnimationSpec
    ) {
        if (operation.mode == IMMEDIATE && animationSpec is SpringSpec<Float>) _interpolator?.overrideAnimationSpec(
            animationSpec
        )
        val animatedSource = animated
        val debugSource = debug
        when {
            (isDebug && debugSource != null) -> debugSource.operation(operation)
            animatedSource == null || DisableAnimations || disableAnimations -> instant.operation(
                operation
            )
            else -> animatedSource.operation(operation, animationSpec)
        }
    }

    private fun onAnimationsStarted() {
        isAnimating = true
    }

    private fun onAnimationsFinished() {
        isAnimating = false
        model.onAnimationFinished()
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
        interpolatorObserverJob?.cancel()
        screenStateJob.cancel()
        scope.cancel()
    }

    fun setNormalisedProgress(progress: Float) {
        debug?.setNormalisedProgress(progress)
    }

    open fun handleBackNavigation(): Boolean = false
}
