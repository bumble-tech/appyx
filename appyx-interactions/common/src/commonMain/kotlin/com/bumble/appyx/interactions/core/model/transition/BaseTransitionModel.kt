package com.bumble.appyx.interactions.core.model.transition

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMPOSED
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.Output
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.SettleDirection
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.utils.multiplatform.AppyxLogger
import com.bumble.appyx.utils.multiplatform.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("TooManyFunctions")
abstract class BaseTransitionModel<InteractionTarget, ModelState : Parcelable>(
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
    private val key: String = KEY_TRANSITION_MODEL,
    private val savedStateMap: SavedStateMap?,
) : TransitionModel<InteractionTarget, ModelState> {
    abstract val initialState: ModelState

    @Suppress("UNCHECKED_CAST")
    private val savedState: ModelState?
        get() = savedStateMap?.get(key) as? ModelState

    abstract fun ModelState.destroyedElements(): Set<Element<InteractionTarget>>

    abstract fun ModelState.removeDestroyedElements(): ModelState

    abstract fun ModelState.removeDestroyedElement(element: Element<InteractionTarget>): ModelState

    abstract fun ModelState.availableElements(): Set<Element<InteractionTarget>>

    override val elements: StateFlow<Set<Element<InteractionTarget>>>
        get() = output
            .map { it.currentTargetState.availableElements() }
            .stateIn(
                scope,
                SharingStarted.Eagerly,
                (savedState ?: initialState).availableElements()
            )

    private val state: MutableStateFlow<Output<ModelState>> by lazy {
        MutableStateFlow(
            Update(
                currentTargetState = savedState ?: initialState,
                animate = false
            )
        )
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        state[key] = this.output.value.currentTargetState
    }

    override val output: StateFlow<Output<ModelState>> by lazy {
        state
    }

    private var enforcedMode: Operation.Mode? = null

    override fun relaxExecutionMode() {
        AppyxLogger.d("BaseTransitionModel", "Relaxing mode")
        enforcedMode = null
        removeDestroyedElements()
    }

    override fun cleanUpElement(element: Element<InteractionTarget>) {
        state.getAndUpdate { output ->
            when (output) {
                is Update<ModelState> -> output.copy(
                    currentTargetState = output.currentTargetState.removeDestroyedElement(element)
                )
                is Keyframes -> output
            }
        }
    }

    private fun removeDestroyedElements() {
        state.getAndUpdate { output ->
            when (output) {
                is Update<ModelState> -> output // TODO
                is Keyframes -> Update(
                    animate = false,
                    currentTargetState = output.currentTargetState.removeDestroyedElements()
                )
            }
        }
    }

    override fun operation(
        operation: Operation<ModelState>,
        overrideMode: Operation.Mode?
    ): Boolean =
        when (enforcedMode ?: overrideMode ?: operation.mode) {
            IMMEDIATE -> {
                // IMMEDIATE mode is kept until UI is settled and model is relaxed
                enforcedMode = IMMEDIATE
                createUpdate(operation)
            }
            IMPOSED -> {
                impose(operation)
            }
            KEYFRAME -> {
                createSegment(operation)
            }
        }

    private fun createUpdate(operation: Operation<ModelState>): Boolean {
        val baseLine = state.value

        return if (operation.isApplicable(baseLine.currentTargetState)) {
            val transition = operation.invoke(baseLine.currentTargetState)
            val newState = baseLine.deriveUpdate(transition)
            updateState(newState)
            true
        } else {
            AppyxLogger.d(TAG, "Operation $operation is not applicable on state: $baseLine")
            false
        }
    }

    private fun impose(operation: Operation<ModelState>): Boolean {
        return when (val currentState = state.value) {
            is Keyframes -> {
                with(currentState) {
                    val past =
                        if (currentIndex > 0) queue.subList(0, currentIndex - 1) else emptyList()
                    val remaining = queue.subList(currentIndex, queue.lastIndex + 1)

                    if (remaining.all { operation.isApplicable(it.targetState) }) {
                        // Replace the operation result into all the queued outputs
                        val newState = copy(
                            queue = past + remaining.map {
                                val newFrom = operation.invoke(it.fromState)
                                val newTarget = operation.invoke(it.targetState)

                                it.copy(
                                    stateTransition = StateTransition(
                                        newFrom.targetState,
                                        newTarget.targetState
                                    )
                                )
                            }
                        )
                        updateState(newState)
                        true
                    } else {
                        AppyxLogger.d(
                            TAG,
                            "Operation $operation is not applicable on one or more queued states: $remaining"
                        )
                        false
                    }
                }
            }
            is Update -> {
                if (operation.isApplicable(currentState.currentTargetState)) {
                    val newState = currentState.deriveUpdate(
                        stateTransition = operation.invoke(currentState.currentTargetState)
                    )
                    updateState(newState)
                    true
                } else {
                    AppyxLogger.d(
                        TAG,
                        "Operation $operation is not applicable on states: $currentState.currentTargetState"
                    )
                    false
                }
            }
        }
    }

    private fun createSegment(operation: Operation<ModelState>): Boolean {
        val currentState = state.value
        val baselineState = currentState.lastTargetState.removeDestroyedElements()

        return if (operation.isApplicable(baselineState)) {
            val transition = operation.invoke(baselineState)
            val newState = currentState.deriveKeyframes(transition)
            updateState(newState)
            true
        } else {
            AppyxLogger.d(TAG, "Operation $operation is not applicable on state: $baselineState")
            false
        }
    }

    override fun setProgress(progress: Float) {
        when (val currentState = state.value) {
            is Update -> {
                AppyxLogger.d(TAG, "Not in keyframe state, ignoring setProgress")
                return
            }
            is Keyframes -> {
                //Do not produce new state because progress is observed
                currentState.setProgress(progress) {
                    // TODO uncomment when method is merged here
                    //  com.bumble.appyx.interactions.core.navigation.BaseNavModel.onTransitionFinished
                    // onTransitionFinished(state.value.fromState.map { it.key })
                }
            }
        }
    }

    override fun onSettled(direction: SettleDirection, animate: Boolean) {
        when (val currentState = state.value) {
            is Update -> {
                AppyxLogger.d(TAG, "Not in keyframe state, nothing to do")
                return
            }
            is Keyframes -> {
                val newState = Update(
                    currentTargetState = when (direction) {
                        SettleDirection.REVERT -> currentState.currentSegment.fromState.removeDestroyedElements()
                        SettleDirection.COMPLETE -> currentState.currentSegment.targetState.removeDestroyedElements()
                    },
                    animate = animate
                )
                updateState(newState)
            }
        }
    }

    private fun updateState(output: Output<ModelState>) {
        // Logger.log(TAG, "Publishing new state ($currentIndex) of queue: ${queue.map { "${it.index}: ${it.javaClass.simpleName}"  }}")
        AppyxLogger.d(TAG, "Publishing new state: $output")
        state.update { output }
    }

    private companion object {
        private const val TAG = "BaseTransitionModel"
        private const val KEY_TRANSITION_MODEL = "TransitionModel"
    }
}
