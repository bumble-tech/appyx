package com.bumble.appyx.interactions

import com.bumble.appyx.interactions.InteractionTarget.Child1
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.operation.Next
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeyframeTest {

    @Test
    fun `When in a segment I can enqueue more segments`() {
        val model = TestDriveModel(
            element = Child1,
            savedStateMap = null
        )

        model.operation(Next())
        model.setProgress(0.5f)
        model.operation(Next())
        model.operation(Next())

        val output = model.output.value
        assertTrue(output is Keyframes)

        (output as Keyframes<InteractionTarget>).let {
            assertEquals(0, output.currentIndex)
            assertEquals(0.5f, output.progress)
        }
    }
}
