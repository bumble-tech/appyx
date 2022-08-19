package com.bumble.appyx.routingsource.spotlight.operation

import com.bumble.appyx.routingsource.spotlight.Spotlight
import com.bumble.appyx.routingsource.spotlight.spotlightElement
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PreviousTest {

    @Test
    fun `Given first element is in transition When previous called Then operation is not applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.INACTIVE_AFTER,
            targetState = Spotlight.TransitionState.INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.ACTIVE,
            targetState = Spotlight.TransitionState.INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `Given first element is not in transition When previous called Then operation is applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.ACTIVE,
            targetState = Spotlight.TransitionState.ACTIVE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing.Routing1,
            fromState = Spotlight.TransitionState.INACTIVE_BEFORE,
            targetState = Spotlight.TransitionState.INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

}
