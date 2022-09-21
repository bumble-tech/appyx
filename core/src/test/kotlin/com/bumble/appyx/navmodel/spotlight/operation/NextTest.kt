package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.navmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_BEFORE
import com.bumble.appyx.navmodel.spotlight.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.spotlight.spotlightElement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class NextTest {

    @Test
    fun `Given last element is in transition When next called Then operation is not applicable`() {
        val firstElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = INACTIVE_AFTER,
            targetState = ACTIVE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertFalse(applicable)
    }

    @Test
    fun `Given last element is not in transition When next called Then operation is applicable`() {
        val firstElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = ACTIVE,
            targetState = ACTIVE,
        )
        val lastElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_AFTER,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Next<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertTrue(applicable)
    }

}
