package com.bumble.appyx.components.internal

import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.model.transition.Update
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyframeToUpdateTest {

    @Test
    fun `When all Keyframes finish Then model switches back to update`() {
        val model = TestDriveModel(
            element = InteractionTarget.Child1,
            savedStateMap = null
        )

        model.operation(Next())

        var output = model.output.value
        assertTrue(output is Keyframes)

        model.onSettled(direction = TransitionModel.SettleDirection.COMPLETE, animate = false)

        output = model.output.value
        assertTrue(output is Update)
    }
}
