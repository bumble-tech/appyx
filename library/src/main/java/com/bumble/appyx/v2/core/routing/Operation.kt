package com.bumble.appyx.v2.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Operation<Routing, State> :
        (RoutingElements<Routing, State>) -> RoutingElements<Routing, State>, Parcelable {

    fun isApplicable(elements: RoutingElements<Routing, State>): Boolean

    @Parcelize
    class Noop<Routing, State> : Operation<Routing, State> {

        override fun isApplicable(elements: RoutingElements<Routing, State>) = false

        override fun invoke(
            elements: RoutingElements<Routing, State>
        ): RoutingElements<Routing, State> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
