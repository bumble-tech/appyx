package com.bumble.appyx.core.routing.source

import com.bumble.appyx.core.routing.RoutingElements
import org.junit.Assert.assertEquals


internal fun <Routing, State> RoutingElements<Routing, State>.assertRoutingElementsEqual(
    elements: RoutingElements<Routing, State>
) {
    assertEquals(size, elements.size)

    forEachIndexed { index, element ->
        val elementToCompare = elements[index]
        assertEquals(element.key.routing, elementToCompare.key.routing)
        assertEquals(element.fromState, elementToCompare.fromState)
        assertEquals(element.targetState, elementToCompare.targetState)
        assertEquals(element.operation, elementToCompare.operation)
    }
}
