package com.bumble.appyx.core.children

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// TODO: test different child lifecycle combinations (except ones MinimumCombinedLifecycle already tested) https://github.com/bumble-tech/appyx/issues/191
// Put here specific cases which should not be tested for both before/after registration
class ChildAwareImplTest : ChildAwareTestBase() {

    @Test
    fun `whenChildAttached is invoked for promoted to eager node`() {
        root = Root().build()
        var capturedNode: Node? = null
        root.whenChildAttachedTest(Child1::class) { _, child ->
            capturedNode = child
        }
        val navKey = NavKey<Configuration>(Configuration.Child1())
        add(navKey)
        val node = root.childOrCreate(navKey).node
        assertEquals(node, capturedNode)
    }

    @Test
    fun `whenChildAttached is invoked only once`() {
        root = Root().build()
        val capturedNodes = mutableListOf<Node>()
        root.whenChildAttachedTest(Child1::class) { _, child ->
            capturedNodes.add(child)
        }
        val navKey = NavKey<Configuration>(Configuration.Child1())
        add(navKey)
        root.childOrCreate(navKey).node
        assertEquals(capturedNodes.size, 1)
    }

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO Later when implemented
    }

    @Test
    fun `whenChildrenAttached is invoked for promoted to eager nodes`() {
        root = Root().build()
        val capturedNodes = HashSet<Pair<Node, Node>>()
        root.whenChildrenAttachedTest(Child1::class, Child2::class) { _, c1, c2 ->
            capturedNodes += c1 to c2
        }
        val navKey1 = NavKey<Configuration>(Configuration.Child1(id = 0))
        val navKey2 = NavKey<Configuration>(Configuration.Child1(id = 1))
        val navKey3 = NavKey<Configuration>(Configuration.Child2(id = 0))
        add(navKey1, navKey2, navKey3)
        val node1 = root.childOrCreate(navKey1).node
        val node2 = root.childOrCreate(navKey2).node
        val node3 = root.childOrCreate(navKey3).node
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
        val navKey1 = NavKey<Configuration>(Configuration.Child1())
        add(navKey1)
        var capturedNode: Node? = null
        root.updateLifecycleState(Lifecycle.State.DESTROYED)
        root.whenChildAttachedTest(Child1::class) { _, child ->
            capturedNode = child
        }
        assertNull(capturedNode)
    }

}
