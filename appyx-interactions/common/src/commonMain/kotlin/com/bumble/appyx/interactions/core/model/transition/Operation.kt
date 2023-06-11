package com.bumble.appyx.interactions.core.model.transition

import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize

interface Operation<ModelState> : Parcelable {

    operator fun invoke(state: ModelState): StateTransition<ModelState>

    enum class Mode {
        /**
         * The operation should be executed without a queue, and should trigger
         * animation mode.
         */
        IMMEDIATE,

        /**
         * Operation should be enqueued and treated as a keyframe.
         */
        KEYFRAME,

        /**
         * The operation should be executed without a queue just the same as IMMEDIATE, but with a
         * special condition: when the model is already in KEYFRAME mode,
         * the operation result should be imposed on all existing keyframe states, rather than
         * switching to IMMEDIATE mode.
         * If the model is already in IMMEDIATE mode, executing an IMPOSED operation isn't
         * different from another IMMEDIATE one.
         *
         * A typical use-case is a state change that should only affect the viewpoint,
         * allowing it to happen always independently of any other element-related state change.
         */
        IMPOSED,
    }

    var mode: Mode

    fun isApplicable(state: ModelState): Boolean


    @Parcelize
    class Noop<ModelState> : Operation<ModelState> {

        override var mode: Mode = Mode.IMMEDIATE

        override fun isApplicable(state: ModelState): Boolean =
            false

        override fun invoke(state: ModelState): StateTransition<ModelState> =
            StateTransition(state, state)

        override fun equals(other: Any?): Boolean = other != null && (this::class == other::class)
        override fun hashCode(): Int = this::class.hashCode()
    }
}
