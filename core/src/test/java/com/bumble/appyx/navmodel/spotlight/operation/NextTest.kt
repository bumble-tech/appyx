package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.navmodel.spotlight.operation.Routing.Routing1
import com.bumble.appyx.navmodel.spotlight.spotlightElement
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NextTest {

    @Test
    fun `Given last element is in transition When next called Then operation is not applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = INACTIVE_AFTER,
            targetState = ACTIVE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `Given last element is not in transition When next called Then operation is applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = ACTIVE,
            targetState = ACTIVE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_AFTER,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

}
