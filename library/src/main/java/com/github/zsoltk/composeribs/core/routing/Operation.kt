package com.github.zsoltk.composeribs.core.routing

interface Operation<T, S> : (RoutingElements<T, S>, UuidGenerator) -> RoutingElements<T, S> {

    fun isApplicable(elements: RoutingElements<T, S>): Boolean
}
