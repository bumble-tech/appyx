package com.bumble.appyx.core.routing.source.spotlight.operation

import com.bumble.appyx.core.routing.Operation.Noop
import com.bumble.appyx.core.routing.source.assertRoutingElementsEqual
import com.bumble.appyx.core.routing.source.spotlight.Routing
import com.bumble.appyx.core.routing.source.spotlight.Routing.Routing1
import com.bumble.appyx.core.routing.source.spotlight.Routing.Routing2
import com.bumble.appyx.core.routing.source.spotlight.Routing.Routing3
import com.bumble.appyx.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.core.routing.source.spotlight.SpotlightElement
import com.bumble.appyx.core.routing.source.spotlight.spotLightElement
import org.junit.Test

internal class UpdateElementsTest {

    @Test
    fun `When operation invoked and no current elements and index is null Then sets new elements and selects 0 index`() {

        val elements = emptyList<SpotlightElement<Routing>>()
        val operation = UpdateElements(listOf(Routing1, Routing2))

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.INACTIVE_AFTER,
                targetState = TransitionState.INACTIVE_AFTER,
                operation = Noop()
            ),
        )

        newElements.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `When operation invoked and no current elements and index is not null Then sets new elements and selects index`() {

        val elements = emptyList<SpotlightElement<Routing>>()
        val operation = UpdateElements(elements = listOf(Routing1, Routing2), initialActiveIndex = 1)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            )
        )

        newElements.assertRoutingElementsEqual(expectedElements)
    }


    @Test
    fun `When operation invoked and currently selected item is contained in new elements and index is null then sets new elements and keeps current selection`() {

        val elements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            )
        )

        val operation = UpdateElements(elements = listOf(Routing1, Routing2, Routing3), initialActiveIndex = null)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing3,
                fromState = TransitionState.INACTIVE_AFTER,
                targetState = TransitionState.INACTIVE_AFTER,
                operation = Noop()
            )
        )

        newElements.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `When operation invoked and currently selected item is contained in new elements and index is non null then sets new elements and selects index`() {

        val elements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            )
        )

        val operation = UpdateElements(elements = listOf(Routing1, Routing2, Routing3), initialActiveIndex = 2)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<SpotlightElement<Routing>>(
            spotLightElement(
                element = Routing1,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing2,
                fromState = TransitionState.INACTIVE_BEFORE,
                targetState = TransitionState.INACTIVE_BEFORE,
                operation = Noop()
            ),
            spotLightElement(
                element = Routing3,
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Noop()
            )
        )

        newElements.assertRoutingElementsEqual(expectedElements)
    }
}
