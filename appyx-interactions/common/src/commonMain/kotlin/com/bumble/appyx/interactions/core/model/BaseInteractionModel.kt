package com.bumble.appyx.interactions.core.model

import DefaultAnimationSpec
import DisableAnimations
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.interactions.core.model.progress.AnimatedProgressController
import com.bumble.appyx.interactions.core.model.progress.DebugProgressInputSource
import com.bumble.appyx.interactions.core.model.progress.DragProgressController
import com.bumble.appyx.interactions.core.model.progress.Draggable
import com.bumble.appyx.interactions.core.model.progress.HasDefaultAnimationSpec
import com.bumble.appyx.interactions.core.model.progress.InstantProgressController
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.context.UiContextAware
import com.bumble.appyx.interactions.core.ui.context.zeroSizeTransitionBounds
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// TODO save/restore state
open class BaseInteractionModel<InteractionTarget : Any, ModelState : Any>(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val model: TransitionModel<InteractionTarget, ModelState>,
    private val motionController: (UiContext) -> MotionController<InteractionTarget, ModelState>,
    private val gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, ModelState> = { GestureFactory.Noop() },
    override val defaultAnimationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    private val backPressStrategy: BackPressHandlerStrategy<InteractionTarget, ModelState> = DontHandleBackPress(),
    private val animateSettle: Boolean = false,
    private val disableAnimations: Boolean = false,
    private val isDebug: Boolean = false
) : InteractionModel<InteractionTarget, ModelState>,
    HasDefaultAnimationSpec<Float>,
    Draggable,
    UiContextAware {
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
                AppyxLogger.d("InteractionModel", "TransitionBounds changed: $value")
                field = value
            }
        }

    private var _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating

    private val instant = InstantProgressController(model = model)
    private var animated: AnimatedProgressController<InteractionTarget, ModelState>? = null
    private var debug: DebugProgressInputSource<InteractionTarget, ModelState>? = null
    private val drag = DragProgressController(
        model = model,
        gestureFactory = { _gestureFactory },
        defaultAnimationSpec = defaultAnimationSpec
    )

    private val _uiModels: MutableStateFlow<List<ElementUiModel<InteractionTarget>>> =
        MutableStateFlow(emptyList())
    val uiModels: StateFlow<List<ElementUiModel<InteractionTarget>>> = _uiModels

    private var elementsJob: Job
    private val _elements: MutableStateFlow<InteractionModel.Elements<InteractionTarget>> =
        MutableStateFlow(InteractionModel.Elements(offScreen = model.elements.value))
    override val elements: StateFlow<InteractionModel.Elements<InteractionTarget>> = _elements

    init {
        // Before motionController is ready we consider all elements as off-screen
        elementsJob = scope.launch {
            model
                .elements
                .collect {
                    _elements.emit(InteractionModel.Elements(offScreen = it))
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
                        AppyxLogger.d("InteractionModel", "Finished animating")
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
                    AppyxLogger.d("InteractionModel", "$it onAnimation finished")
                    model.cleanUpElement(it)
                }
        }
    }

    override fun onAddedToComposition(scope: CoroutineScope) {
        animationScope = scope
        createAnimatedInputSource(scope)
        createdDebugInputSource(scope)
    }

    override fun onRemovedFromComposition() {
        // TODO finish unfinished transitions
        if (isDebug) debug?.stopModel() else animated?.stopModel()
        animationScope?.cancel()
    }

    private fun createAnimatedInputSource(scope: CoroutineScope) {
        animated = AnimatedProgressController(
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
        observeAnimationChanges(motionController)
        observeMotionController(motionController)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMotionController(motionController: MotionController<InteractionTarget, ModelState>) {
        elementsJob.cancel()
        motionControllerObserverJob?.cancel()
        motionControllerObserverJob = scope.launch {
            model
                .output
                .flatMapLatest { motionController.map(it) }
                .flatMapLatest { elementUiModels ->
                    val visibilityFlows = elementUiModels.map { uiModel ->
                        uiModel.visibleState
                    }
                    combine(visibilityFlows) { visibilityValues ->
                        val onScreen = mutableSetOf<Element<InteractionTarget>>()
                        val offScreen = mutableSetOf<Element<InteractionTarget>>()
                        visibilityValues.forEachIndexed { index, visibilityValue ->
                            val element = elementUiModels[index].element
                            if (visibilityValue) {
                                onScreen.add(element)
                            } else {
                                offScreen.add(element)
                            }
                        }
                        InteractionModel.Elements(
                            onScreen = onScreen,
                            offScreen = offScreen
                        ) to elementUiModels
                    }
                }
                .collect { (elementsState, elementUiModels) ->
                    // Order is important here. We need to report screen state to the ParentNode
                    // first, before elementUiModels are consumed by the UI
                    _elements.emit(elementsState)
                    _uiModels.emit(elementUiModels)
                }
        }
    }

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
        _isAnimating.update { true }
    }

    private fun onAnimationsFinished() {
        model.relaxExecutionMode()
        _isAnimating.update { false }
    }

    override fun onStartDrag(position: Offset) {
        drag.onStartDrag(position)
    }

    override fun onDrag(dragAmount: Offset, density: Density) {
        if (!_isAnimating.value) {
            drag.onDrag(dragAmount, density)
        }
    }

    override fun onDragEnd(
        completionThreshold: Float,
        completeGestureSpec: AnimationSpec<Float>,
        revertGestureSpec: AnimationSpec<Float>
    ) {
        if (!_isAnimating.value) {
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
    override fun destroy() {
        motionControllerObserverJob?.cancel()
        elementsJob.cancel()
        scope.cancel()
    }

    fun setNormalisedProgress(progress: Float) {
        debug?.setNormalisedProgress(progress)
    }

    override fun handleBackPress(): Boolean = backPressStrategy.handleBackPress()

    override fun canHandeBackPress(): StateFlow<Boolean> = backPressStrategy.canHandleBackPress

    override fun saveInstanceState(state: MutableSavedStateMap) {
        model.saveInstanceState(state)
    }
}
