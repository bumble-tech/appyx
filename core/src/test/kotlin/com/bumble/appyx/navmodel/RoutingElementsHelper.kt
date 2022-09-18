package com.bumble.appyx.navmodel

import com.bumble.appyx.core.navigation.NavElements
import org.junit.Assert.assertEquals


internal fun <Routing, State> NavElements<Routing, State>.assertRoutingElementsEqual(
    elements: NavElements<Routing, State>
) {
    assertEquals(size, elements.size)

    forEachIndexed { index, element ->
        val elementToCompare = elements[index]
        assertEquals(element.key.navTarget, elementToCompare.key.navTarget)
        assertEquals(element.fromState, elementToCompare.fromState)
        assertEquals(element.targetState, elementToCompare.targetState)
        assertEquals(element.operation, elementToCompare.operation)
    }
}
