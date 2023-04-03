package com.bumble.appyx.interactions

import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateToKeyframeTest {

    @Test
    fun `When animation hasn't settled Then keyframe is interpreted as update`() {
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
    fun `When animation has settled Then keyframe can be deployed`() {
        val model = TestDriveModel(
            element = InteractionTarget.Child1,
            savedStateMap = null
        )

        model.operation(Next(mode = Operation.Mode.IMMEDIATE))
        assertTrue(model.output.value is Update)

        model.onSettled(direction = TransitionModel.SettleDirection.COMPLETE)

        model.operation(Next(mode = Operation.Mode.KEYFRAME))
        assertTrue(model.output.value is Keyframes)
    }

}
