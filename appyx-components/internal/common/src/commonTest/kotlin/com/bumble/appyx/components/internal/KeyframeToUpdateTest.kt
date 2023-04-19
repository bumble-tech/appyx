package com.bumble.appyx.components.internal

import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.model.transition.Update
import kotlin.test.Test
import kotlin.test.assertTrue

class KeyframeToUpdateTest {

    @Test
    fun When_all_Keyframes_finish_Then_model_switches_back_to_update() {
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
