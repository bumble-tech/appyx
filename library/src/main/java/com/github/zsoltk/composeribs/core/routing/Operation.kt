package com.github.zsoltk.composeribs.core.routing

interface Operation<T, S> : (RoutingElements<T, S>, UuidGenerator) -> RoutingElements<T, S> {

    fun isApplicable(elements: RoutingElements<T, S>): Boolean

    class Noop<T, S> : Operation<T, S> {

        override fun isApplicable(elements: RoutingElements<T, S>) = false

        override fun invoke(
            elements: RoutingElements<T, S>,
            uuidGenerator: UuidGenerator
        ): RoutingElements<T, S> = elements
    }
}
