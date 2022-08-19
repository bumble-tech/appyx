package com.bumble.appyx.routingsource.spotlight.operation

import com.bumble.appyx.routingsource.spotlight.Spotlight
import com.bumble.appyx.routingsource.spotlight.spotlightElement
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NextTest {

    @Test
    fun `Given last element is in transition When next called Then operation is not applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.INACTIVE_AFTER,
            targetState = Spotlight.TransitionState.INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.INACTIVE_AFTER,
            targetState = Spotlight.TransitionState.ACTIVE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `Given last element is not in transition When next called Then operation is applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.ACTIVE,
            targetState = Spotlight.TransitionState.ACTIVE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.INACTIVE_AFTER,
            targetState = Spotlight.TransitionState.INACTIVE_AFTER,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

}
