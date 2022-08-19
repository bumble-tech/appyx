package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.navmodel.spotlight.operation.Routing.Routing1
import com.bumble.appyx.navmodel.spotlight.spotlightElement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class PreviousTest {

    @Test
    fun `Given first element is in transition When previous called Then operation is not applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = ACTIVE,
            targetState = INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<Routing>()

        val applicable = operation.isApplicable(elements)

        assertFalse(applicable)
    }

    @Test
    fun `Given first element is not in transition When previous called Then operation is applicable`() {
        val firstElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = ACTIVE,
            targetState = ACTIVE,
        )
        val lastElement = spotlightElement<Routing>(
            element = Routing1,
            fromState = INACTIVE_BEFORE,
            targetState = INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<Routing>()

        val applicable = operation.isApplicable(elements)

        assertTrue(applicable)
    }

}
