package com.bumble.appyx.components.internal

import com.bumble.appyx.components.internal.InteractionTarget.Child1
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.operation.Next
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyframeTest {

    @Test
    fun When_in_a_segment_I_can_enqueue_more_segments() {
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
