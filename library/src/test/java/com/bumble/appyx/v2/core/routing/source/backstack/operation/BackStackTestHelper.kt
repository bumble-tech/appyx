package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import org.junit.Assert.assertEquals

internal sealed class Routing {
    object Routing1 : Routing()
    object Routing2 : Routing()
    object Routing3 : Routing()
    data class Routing4(val dummy: String) : Routing()
}

internal fun <T : Routing> backStackElement(
    element: T,
    key: RoutingKey<T> = RoutingKey(routing = element),
    fromState: BackStack.TransitionState,
    targetState: BackStack.TransitionState,
    operation: Operation<T, BackStack.TransitionState>
) = BackStackElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)

internal fun BackStackElements<Routing>.assertBackstackElementsEqual(elements: BackStackElements<Routing>) {
    assertEquals(size, elements.size)

    forEachIndexed { index, element ->
        val elementToCompare = elements[index]
        assertEquals(element.key.routing, elementToCompare.key.routing)
        assertEquals(element.fromState, elementToCompare.fromState)
        assertEquals(element.targetState, elementToCompare.targetState)
        assertEquals(element.operation, elementToCompare.operation)
    }
}
