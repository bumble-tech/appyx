package com.github.zsoltk.composeribs.core.children

import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.Node
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// TODO: test different child lifecycle combinations (except ones MinimumCombinedLifecycle already tested)
// Put here specific cases which should not be tested for both before/after registration
class ChildAwareImplTest : ChildAwareTestBase() {

    @Test
    fun `whenChildAttached is invoked for promoted to eager node`() {
        root = Root(childMode = ChildEntry.ChildMode.LAZY)
        var capturedNode: Node? = null
        root.whenChildAttached<Child1> { _, child ->
            capturedNode = child
        }
        val configuration = Configuration.Child1()
        add(configuration)
        val node = root.childOrCreate(TestRoutingSource.RoutingKeyImpl(configuration)).node
        assertEquals(node, capturedNode)
    }

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO Later when implemented
    }

    @Test
    fun `whenChildrenAttached is invoked for promoted to eager nodes`() {
        root = Root(childMode = ChildEntry.ChildMode.LAZY)
        val capturedNodes = HashSet<Pair<Node, Node>>()
        root.whenChildrenAttached<Child1, Child2> { _, c1, c2 ->
            capturedNodes += c1 to c2
        }
        val configuration1 = Configuration.Child1(id = 0)
        val configuration2 = Configuration.Child1(id = 1)
        val configuration3 = Configuration.Child2(id = 0)
        add(configuration1, configuration2, configuration3)
        val node1 = root.childOrCreate(TestRoutingSource.RoutingKeyImpl(configuration1)).node
        val node2 = root.childOrCreate(TestRoutingSource.RoutingKeyImpl(configuration2)).node
        val node3 = root.childOrCreate(TestRoutingSource.RoutingKeyImpl(configuration3)).node
        assertEquals(
            setOf(
                node1 to node3,
                node2 to node3,
            ),
            capturedNodes
        )
    }

    @Test
    fun `whenChildrenAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO Later when implemented
    }

    @Test
    fun `ignores registration when parent lifecycle is destroyed`() {
        add(Configuration.Child1())
        var capturedNode: Node? = null
        root.updateLifecycleState(Lifecycle.State.DESTROYED)
        root.whenChildAttached<Child1> { _, child ->
            capturedNode = child
        }
        assertNull(capturedNode)
    }

}
