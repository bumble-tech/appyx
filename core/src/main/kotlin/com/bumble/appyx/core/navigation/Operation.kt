package com.bumble.appyx.core.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Operation<NavTarget, State> :
        (NavElements<NavTarget, State>) -> NavElements<NavTarget, State>, Parcelable {

    fun isApplicable(elements: NavElements<NavTarget, State>): Boolean

    @Parcelize
    class Noop<NavTarget, State> : Operation<NavTarget, State> {

        override fun isApplicable(elements: NavElements<NavTarget, State>) = false

        override fun invoke(
            elements: NavElements<NavTarget, State>
        ): NavElements<NavTarget, State> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
