package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize

interface Operation<ModelState> : (ModelState) -> NavTransition<ModelState>, Parcelable {

    enum class Mode {
        IMMEDIATE, KEYFRAME
    }

    val mode: Mode

    fun isApplicable(state: ModelState): Boolean


    @Parcelize
    class Noop<ModelState> : Operation<ModelState> {

        override val mode: Mode
            get() = Mode.IMMEDIATE

        override fun isApplicable(state: ModelState): Boolean =
            false

        override fun invoke(state: ModelState): NavTransition<ModelState> =
            NavTransition(state, state)

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass
        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
