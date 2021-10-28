package com.github.zsoltk.composeribs.core.children

import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.Node
import org.junit.Assert.assertNull
import org.junit.Test

// TODO: test different child lifecycle combinations (except ones MinimumCombinedLifecycle already tested)
// Put here specific cases which should not be tested for both before/after registration
class ChildAwareImplTest : ChildAwareTestBase() {

    @Test
    fun `whenChildAttached is invoked for promoted to eager node`() {
        // TODO
    }

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO
    }

    @Test
    fun `whenChildrenAttached is invoked for promoted to eager nodes`() {
        // TODO
    }

    @Test
    fun `whenChildrenAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO
    }

    @Test
    fun `ignores registration when parent lifecycle is destroyed`() {
        add(Configuration.Child1())
        var capturedNode: Node<*>? = null
        root.updateLifecycleState(Lifecycle.State.DESTROYED)
        root.whenChildAttached<Child1> { _, child ->
            capturedNode = child
        }
        assertNull(capturedNode)
    }

}
