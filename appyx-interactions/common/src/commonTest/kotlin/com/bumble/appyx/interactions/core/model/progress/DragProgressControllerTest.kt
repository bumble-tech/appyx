package com.bumble.appyx.interactions.core.model.progress

import DefaultAnimationSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.InteractionTarget.Child1
import com.bumble.appyx.InteractionTarget.Child2
import com.bumble.appyx.interactions.core.TestGestures
import com.bumble.appyx.interactions.core.TestTransitionModel
import kotlin.test.Test
import kotlin.test.asserter

class DragProgressControllerTest {

    @Test
    fun GIVEN_drag_occurs_WHEN_same_drag_amount_is_still_remaining_THEN_no_error_raises() {
        val items = listOf(
            Child1,
            Child2,
        )
        val gestureFactory = TestGestures(Child2)
        val dragProgressController = DragProgressController(
            model = TestTransitionModel(items),
            gestureFactory = { gestureFactory },
            defaultAnimationSpec = DefaultAnimationSpec,
        )
        try {
            dragProgressController.onDrag(Offset(-12f, 0f), Density(1f))
        } catch (any: Throwable) {
            asserter.fail("No exception expected but $any")
        }
    }

}
