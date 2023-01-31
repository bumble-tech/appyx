package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.*
import com.bumble.appyx.interactions.core.TransitionModel.Output
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

@SuppressWarnings("UnusedPrivateMember")
abstract class BaseTransitionModel<NavTarget, ModelState>(
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
) : TransitionModel<NavTarget, ModelState> {
    abstract val initialState: ModelState

    abstract fun ModelState.destroyedElements(): Set<NavElement<NavTarget>>

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

    override fun relaxExecutionMode() {
        enforcedMode = null
    }

    override fun operation(operation: Operation<ModelState>, overrideMode: Operation.Mode?): Boolean =
        when (enforcedMode ?: overrideMode ?: operation.mode) {
            IMMEDIATE -> {
                // Replacing while in keyframes mode triggers enforced IMMEDIATE execution as a side effect
                if (state.value is Keyframes) {
                    enforcedMode = IMMEDIATE
                }
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
            val transition = operation.invoke(baseLine.currentTargetState)
            val newState = baseLine.deriveUpdate(transition)
            updateState(newState)
            true
        } else {
            Logger.log(TAG, "Operation $operation is not applicable on state: $baseLine")
            false
        }
    }

    private fun updateGeometry(operation: Operation<ModelState>): Boolean {
        when (val currentState = state.value) {
            is Keyframes -> {
                with(currentState) {
                    val past = if (currentIndex > 0) queue.subList(0, currentIndex - 1) else emptyList()
                    val remaining = queue.subList(currentIndex, queue.lastIndex)

                    return if (remaining.all { operation.isApplicable(it.targetState) }) {
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
            is Update -> TODO()
        }
    }

    private fun createSegment(operation: Operation<ModelState>): Boolean {
        val currentState = state.value
        val baselineState = currentState.lastTargetState

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
                // val progress = progress.coerceAtLeast(1f)
                val newState = currentState.setProgress(progress) {
                    // TODO uncomment when method is merged here
                    //  com.bumble.appyx.interactions.core.navigation.BaseNavModel.onTransitionFinished
                    // onTransitionFinished(state.value.fromState.map { it.key })
                }

                updateState(newState)
            }
        }
    }

    override fun dropAfter(segmentIndex: Int) {
        when (val currentState = state.value) {
            is Update -> {
                Logger.log(TAG, "Not in keyframe state, ignoring dropAfter")
                return
            }
            is Keyframes -> {
                val newState = currentState.dropAfter(segmentIndex)
                updateState(newState)
            }
        }
    }

    private fun updateState(output: Output<ModelState>) {
        // Logger.log(TAG, "Publishing new state ($currentIndex) of queue: ${queue.map { "${it.index}: ${it.javaClass.simpleName}"  }}")
        Logger.log(TAG, "Publishing new state: ${this.output.value}")
        state.update { output }
    }

    private companion object {
        private val TAG = BaseTransitionModel::class.java.name
    }
}
