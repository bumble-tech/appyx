package com.bumble.appyx.components.internal

import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.Update
import kotlin.test.Test
import kotlin.test.assertTrue

class UpdateToKeyframeTest {

    @Test
    fun When_animation_hasn_not_settled_Then_keyframe_is_interpreted_as_update() {
        val model = TestDriveModel(
            element = InteractionTarget.Child1,
            savedStateMap = null
        )

        model.operation(Next(mode = Operation.Mode.IMMEDIATE))
        assertTrue(model.output.value is Update)
        model.operation(Next(mode = Operation.Mode.KEYFRAME))
        assertTrue(model.output.value is Update)
    }

    @Test
    fun When_animation_has_settled_Then_keyframe_can_be_deployed() {
        val model = TestDriveModel(
            element = InteractionTarget.Child1,
            savedStateMap = null
        )

        model.operation(Next(mode = Operation.Mode.IMMEDIATE))
        assertTrue(model.output.value is Update)

        model.relaxExecutionMode()

        model.operation(Next(mode = Operation.Mode.KEYFRAME))
        assertTrue(model.output.value is Keyframes)
    }

}
