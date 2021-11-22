package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Operation<T, S> : (RoutingElements<T, S>) -> RoutingElements<T, S>, Parcelable {

    fun isApplicable(elements: RoutingElements<T, S>): Boolean

    @Parcelize
    class Noop<T, S> : Operation<T, S> {

        override fun isApplicable(elements: RoutingElements<T, S>) = false

        override fun invoke(
            elements: RoutingElements<T, S>
        ): RoutingElements<T, S> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
