package com.bumble.appyx.interactions.core.model.transition

import com.bumble.appyx.interactions.core.NavElement
import kotlinx.coroutines.flow.StateFlow

interface TransitionModel<InteractionTarget, ModelState> {

    val output: StateFlow<Output<ModelState>>

    sealed class Output<ModelState> {
        abstract val currentTargetState: ModelState

        abstract val lastTargetState: ModelState

        abstract fun replace(targetState: ModelState): Output<ModelState>

        abstract fun deriveKeyframes(navTransition: NavTransition<ModelState>): Keyframes<ModelState>

        abstract fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState>

    }

    fun availableElements(): StateFlow<Set<NavElement<InteractionTarget>>>

    fun relaxExecutionMode()

    fun cleanUpElement(navElement: NavElement<InteractionTarget>)

    fun operation(
        operation: Operation<ModelState>,
        overrideMode: Operation.Mode? = null
    ): Boolean

    fun setProgress(progress: Float)

    fun onSettled(direction: SettleDirection, animate: Boolean = false)

    enum class SettleDirection {
        REVERT, COMPLETE
    }
}
