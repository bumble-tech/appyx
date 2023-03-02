package com.bumble.appyx.interactions.core

import DefaultAnimationSpec
import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.progress.AnimatedInputSource
import com.bumble.appyx.interactions.core.model.progress.DebugProgressInputSource
import com.bumble.appyx.interactions.core.model.progress.DragProgressController
import com.bumble.appyx.interactions.core.model.progress.Draggable
import com.bumble.appyx.interactions.core.model.progress.HasDefaultAnimationSpec
import com.bumble.appyx.interactions.core.model.progress.InstantInputSource
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.ScreenState
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.context.UiContextAware
import com.bumble.appyx.interactions.core.ui.context.zeroSizeTransitionBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// TODO save/restore state
open class InteractionModel<InteractionTarget : Any, ModelState : Any>(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: TransitionModel<InteractionTarget, ModelState>,
    private val motionController: (UiContext) -> MotionController<InteractionTarget, ModelState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, ModelState> = { GestureFactory.Noop() },
    override val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val backPressStrategy: BackPressHandlerStrategy<InteractionTarget, ModelState> = DontHandleBackPress(),
    private val animateSettle: Boolean = false,
    private val disableAnimations: Boolean = false,
    private val isDebug: Boolean = false
) : HasDefaultAnimationSpec<Float>, Draggable, UiContextAware {
    init {
        backPressStrategy.init(this, model)
    }

    private var motionControllerObserverJob: Job? = null
    private var _motionController: MotionController<InteractionTarget, ModelState>? = null

    private var _gestureFactory: GestureFactory<InteractionTarget, ModelState> =
        gestureFactory(zeroSizeTransitionBounds)

    private var animationChangesJob: Job? = null
    private var animationFinishedJob: Job? = null

    private var transitionBounds: TransitionBounds = zeroSizeTransitionBounds
        set(value) {
            if (value != field) {
                Logger.log("InteractionModel", "TransitionBounds changed: $value")
                field = value
            }
        }

    private var isAnimating: Boolean = false
    private val instant = InstantInputSource(model = model)
    private var animated: AnimatedInputSource<InteractionTarget, ModelState>? = null
    private var debug: DebugProgressInputSource<InteractionTarget, ModelState>? = null
    private val drag = DragProgressController(
        model = model,
        gestureFactory = { _gestureFactory },
        defaultAnimationSpec = defaultAnimationSpec
    )

    private val _frames: MutableStateFlow<List<FrameModel<InteractionTarget>>> =
        MutableStateFlow(emptyList())
    val frames: StateFlow<List<FrameModel<InteractionTarget>>> = _frames

    private var screenStateJob: Job
    private val _screenState: MutableStateFlow<ScreenState<InteractionTarget>> =
        MutableStateFlow(ScreenState(offScreen = model.availableElements().value))
    val screenState: StateFlow<ScreenState<InteractionTarget>> = _screenState

    private val _clipToBounds: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val clipToBounds: StateFlow<Boolean> = _clipToBounds


    init {
        // Before motionController is ready we consider all elements as off-screen
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

    private fun observeAnimationChanges(motionController: MotionController<InteractionTarget, ModelState>) {
        animationChangesJob?.cancel()
        animationChangesJob = scope.launch {
            motionController.isAnimating()
                .collect {
                    if (!it) {
                        Logger.log("InteractionModel", "Finished animating")
                        onAnimationsFinished()
                    } else {
                        onAnimationsStarted()
                    }
                }
        }
        animationFinishedJob?.cancel()
        animationFinishedJob = scope.launch {
            motionController.finishedAnimations
                .collect {
                    Logger.log("InteractionModel", "$it onAnimation finished")
                    model.cleanUpElement(it)
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
            _motionController = motionController(uiContext).also {
                onMotionControllerReady(it)
            }
            _gestureFactory = gestureFactory(transitionBounds)
        }
    }

    private fun onMotionControllerReady(motionController: MotionController<InteractionTarget, ModelState>) {
        _clipToBounds.update { motionController.clipToBounds }
        observeAnimationChanges(motionController)
        observeMotionController(motionController)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMotionController(motionController: MotionController<InteractionTarget, ModelState>) {
        screenStateJob.cancel()
        motionControllerObserverJob?.cancel()
        motionControllerObserverJob = scope.launch {
            model
                .output
                .flatMapLatest { motionController.map(it) }
                .flatMapLatest { frames ->
                    val frameVisibilityFlows = frames.map { frame ->
                        frame.visibleState
                    }
                    combine(frameVisibilityFlows) { visibilityValues ->
                        val onScreen = mutableSetOf<Element<InteractionTarget>>()
                        val offScreen = mutableSetOf<Element<InteractionTarget>>()
                        visibilityValues.forEachIndexed { index, visibilityValue ->
                            val element = frames[index].element
                            if (visibilityValue) {
                                onScreen.add(element)
                            } else {
                                offScreen.add(element)
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

    fun availableElements(): StateFlow<Set<Element<InteractionTarget>>> = model.availableElements()

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float>? = null
    ) {
        if (operation.mode == IMMEDIATE && animationSpec is SpringSpec<Float>) _motionController?.overrideAnimationSpec(
            animationSpec
        )
        val animatedSource = animated
        val debugSource = debug
        when {
            (isDebug && debugSource != null) -> debugSource.operation(operation)
            animatedSource == null || DisableAnimations || disableAnimations -> instant.operation(
                operation
            )
            else -> animatedSource.operation(operation, animationSpec ?: defaultAnimationSpec)
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
        motionControllerObserverJob?.cancel()
        screenStateJob.cancel()
        scope.cancel()
    }

    fun setNormalisedProgress(progress: Float) {
        debug?.setNormalisedProgress(progress)
    }

    open fun handleBackPress(): Boolean = backPressStrategy.handleBackPress()

    open fun canHandeBackPress(): Flow<Boolean> = backPressStrategy.canHandleBackPress
}
