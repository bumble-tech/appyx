package com.bumble.appyx.interactions.model.transition

import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.plugin.SavesInstanceState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface TransitionModel<InteractionTarget, ModelState> : SavesInstanceState {

    val output: StateFlow<Output<ModelState>>

    @Serializable
    sealed class Output<ModelState> {
        abstract val currentTargetState: ModelState

        abstract val lastTargetState: ModelState

        abstract fun deriveKeyframes(stateTransition: StateTransition<ModelState>): Keyframes<ModelState>

        abstract fun deriveUpdate(stateTransition: StateTransition<ModelState>): Update<ModelState>

    }

    val elements: StateFlow<Set<Element<InteractionTarget>>>

    fun relaxExecutionMode()

    fun cleanUpElement(element: Element<InteractionTarget>)

    fun operation(
        operation: Operation<ModelState>,
        overrideMode: Operation.Mode? = null
    ): Boolean

    fun canApply(operation: Operation<ModelState>): Boolean =
        operation.isApplicable(output.value.currentTargetState)

    fun setProgress(progress: Float)

    fun onSettled(direction: SettleDirection, animate: Boolean = false)

    enum class SettleDirection {
        REVERT, COMPLETE
    }
}
