package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ActivateTest {

    @Test
    fun `When new index is the same Then operation isn't applicable`() {
        val state = SpotlightModel.State(
            standard = listOf<NavElement<NavTarget>>(),
            activeIndex = 0f,
            activeWindow = 1f
        )

        val activate = Activate<NavTarget>(0f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun `When new index is different And out of range Then operation isn't applicable`() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val activate = Activate<NavTarget>(2f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun `When new index is different And is withing range Then active index is updated`() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val activate = Activate<NavTarget>(1f)

        assertEquals(
            actual = activate.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
