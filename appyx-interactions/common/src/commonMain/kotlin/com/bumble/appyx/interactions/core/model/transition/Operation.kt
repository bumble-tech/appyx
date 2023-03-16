package com.bumble.appyx.interactions.core.model.transition

import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize

interface Operation<ModelState> : (ModelState) -> StateTransition<ModelState>, Parcelable {

    enum class Mode {
        /**
         * Operation should be executed without a queue, and should trigger
         * animation mode.
         */
        IMMEDIATE,

        /**
         * Operation should be executed without a queue just as IMMEDIATE,
         * but shouldn't trigger animation mode. It's intended for state changes
         * that should affect geometry only.
         */
        GEOMETRY,

        /**
         * Operation should be enqueued and treated as a keyframe.
         */
        KEYFRAME
    }

    val mode: Mode

    fun isApplicable(state: ModelState): Boolean


    @Parcelize
    class Noop<ModelState> : Operation<ModelState> {

        override val mode: Mode
            get() = Mode.IMMEDIATE

        override fun isApplicable(state: ModelState): Boolean =
            false

        override fun invoke(state: ModelState): StateTransition<ModelState> =
            StateTransition(state, state)

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass
        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
