package com.bumble.appyx.core.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Operation<Routing, State> :
        (NavElements<Routing, State>) -> NavElements<Routing, State>, Parcelable {

    fun isApplicable(elements: NavElements<Routing, State>): Boolean

    @Parcelize
    class Noop<Routing, State> : Operation<Routing, State> {

        override fun isApplicable(elements: NavElements<Routing, State>) = false

        override fun invoke(
            elements: NavElements<Routing, State>
        ): NavElements<Routing, State> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
