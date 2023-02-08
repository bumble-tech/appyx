package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.*
import com.bumble.appyx.interactions.core.TransitionModel.Output
import com.bumble.appyx.interactions.core.TransitionModel.SettleDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

@SuppressWarnings("UnusedPrivateMember")
abstract class BaseTransitionModel<NavTarget, ModelState>(
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : TransitionModel<NavTarget, ModelState> {
    abstract val initialState: ModelState

    abstract fun ModelState.destroyedElements(): Set<NavElement<NavTarget>>

    abstract fun ModelState.removeDestroyedElements(): ModelState

    abstract fun ModelState.availableElements(): Set<NavElement<NavTarget>>

    override fun availableElements(): Set<NavElement<NavTarget>> =
        state.value.currentTargetState.availableElements()

    private val state: MutableStateFlow<Output<ModelState>> by lazy {
        MutableStateFlow(
            Update(
                currentTargetState = initialState,
                animate = false
            )
        )
    }

    override val output: StateFlow<Output<ModelState>> by lazy {
        state
    }

    private var enforcedMode: Operation.Mode? = null

    override fun onAnimationFinished() {
        Logger.log("BaseTransitionModel", "Relaxing mode")
        enforcedMode = null
        removeDestroyedElements()
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
            GEOMETRY -> {
                updateGeometry(operation)
            }
            KEYFRAME -> {
                createSegment(operation)
            }
        }

    private fun createUpdate(operation: Operation<ModelState>): Boolean {
        val baseLine = state.value

        return if (operation.isApplicable(baseLine.currentTargetState)) {
            val transition = operation.invoke(baseLine.currentTargetState.removeDestroyedElements())
            val newState = baseLine.deriveUpdate(transition)
            updateState(newState)
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $baseLine")
            false
        }
    }

    private fun updateGeometry(operation: Operation<ModelState>): Boolean {
        return when (val currentState = state.value) {
            is Keyframes -> {
                with(currentState) {
                    val past = if (currentIndex > 0) queue.subList(0, currentIndex - 1) else emptyList()
                    val remaining = queue.subList(currentIndex, queue.lastIndex + 1)

                    if (remaining.all { operation.isApplicable(it.targetState) }) {
                        // Replace the operation result into all the queued outputs
                        val newState = copy(
                            queue = past + remaining.map {
                                it.copy(
                                    navTransition = operation.invoke(it.targetState)
                                )
                            }
                        )
                        updateState(newState)
                        true
                    } else {
                        Logger.log(TAG, "Operation $operation is not applicable on one or more queued states: $remaining")
                        false
                    }
                }
            }
            is Update -> {
                if (operation.isApplicable(currentState.currentTargetState)) {
                    val newState = currentState.deriveUpdate(
                        navTransition = operation.invoke(currentState.currentTargetState)
                    )
                    updateState(newState)
                    true
                }
                Logger.log(TAG, "Operation $operation is not applicable on states: $currentState.currentTargetState")
                false
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
            Logger.log(TAG, "Operation $operation is not applicable on state: $baselineState")
            false
        }
    }

    override fun setProgress(progress: Float) {
        when (val currentState = state.value) {
            is Update -> {
                Logger.log(TAG, "Not in keyframe state, ignoring setProgress")
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
                Logger.log(TAG, "Not in keyframe state, nothing to do")
                return
            }
            is Keyframes -> {
                val newState = Update(
                    currentTargetState = when (direction) {
                        SettleDirection.REVERT -> currentState.currentSegment.fromState
                        SettleDirection.COMPLETE -> currentState.currentSegment.targetState
                    },
                    animate = animate
                )
                updateState(newState)
            }
        }
    }

    private fun updateState(output: Output<ModelState>) {
        // Logger.log(TAG, "Publishing new state ($currentIndex) of queue: ${queue.map { "${it.index}: ${it.javaClass.simpleName}"  }}")
        Logger.log(TAG, "Publishing new state: $output")
        state.update { output }
    }

    private companion object {
        private val TAG = BaseTransitionModel::class.java.name
    }
}
