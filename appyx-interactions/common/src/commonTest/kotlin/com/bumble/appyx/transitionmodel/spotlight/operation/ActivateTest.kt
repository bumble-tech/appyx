package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ActivateTest {

    @Test
    fun WHEN_new_index_is_the_same_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            standard = listOf<NavElement<NavTarget>>(),
            activeIndex = 0f,
            activeWindow = 1f
        )

        val activate = Activate<NavTarget>(0f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun WHEN_new_index_is_different_AND_out_of_range_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val activate = Activate<NavTarget>(2f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun WHEN_new_index_is_different_AND_is_withing_range_THEN_active_index_is_updated() {
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
