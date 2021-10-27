package com.github.zsoltk.composeribs.core.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.LeafNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// TODO: test different child lifecycle combinations (except ones MinimumCombinedLifecycle already tested)
class ChildAwareImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var root: Root

    @Before
    fun before() {
        root = Root()
    }

    // region Single

    @Test
    fun `whenChildAttached is invoked if registered before onChildAttached`() {
        var capturedNode: Child1? = null
        root.whenChildAttached<Child1> { _, child -> capturedNode = child }
        val child = add(Configuration.Child1())
        assertEquals(child, capturedNode)
    }

    @Test
    fun `whenChildAttached is invoked if registered after onChildAttached`() {
        var capturedNode: Child1? = null
        val child = add(Configuration.Child1())
        root.whenChildAttached<Child1> { _, child -> capturedNode = child }
        assertEquals(child, capturedNode)
    }

    @Test
    fun `every whenChildAttached is invoked`() {
        var capturedNode1: Child1? = null
        var capturedNode2: Child1? = null
        root.whenChildAttached<Child1> { _, child -> capturedNode1 = child }
        root.whenChildAttached<Child1> { _, child -> capturedNode2 = child }
        val child = add(Configuration.Child1())
        assertEquals(child, capturedNode1)
        assertEquals(child, capturedNode2)
    }

    @Test
    fun `whenChildAttached is invoked multiple times for each instance`() {
        val child1 = add(Configuration.Child1(id = 0))
        val child2 = add(Configuration.Child1(id = 1))
        val capturedNodes = ArrayList<Child1>()
        root.whenChildAttached<Child1> { _, child -> capturedNodes += child }
        assertEquals(listOf(child1, child2), capturedNodes)
    }

    @Test
    fun `whenChildAttached is not invoked for unrelated child`() {
        var capturedNode: Child2? = null
        root.whenChildAttached<Child2> { _, child -> capturedNode = child }
        assertNull(capturedNode)
    }

    // endregion

    // region Double

    @Test
    fun `whenChildrenAttached is invoked if registered before onChildAttached`() {
        val capturedNodes = ArrayList<Node<*>>()
        root.whenChildrenAttached<Child1, Child2> { _, c1, c2 ->
            capturedNodes += c1
            capturedNodes += c2
        }
        val child1 = add(Configuration.Child1())
        val child2 = add(Configuration.Child2())
        assertEquals(listOf(child1, child2), capturedNodes)
    }

    @Test
    fun `whenChildrenAttached is invoked if registered after onChildAttached`() {
        val child1 = add(Configuration.Child1())
        val child2 = add(Configuration.Child2())
        val capturedNodes = ArrayList<Node<*>>()
        root.whenChildrenAttached<Child1, Child2> { _, c1, c2 ->
            capturedNodes += c1
            capturedNodes += c2
        }
        assertEquals(listOf(child1, child2), capturedNodes)
    }

    @Test
    fun `whenChildrenAttached is not invoked for unrelated children`() {
        add(Configuration.Child1())
        add(Configuration.Child3())
        val capturedNodes = ArrayList<Node<*>>()
        root.whenChildrenAttached<Child1, Child2> { _, c1, c2 ->
            capturedNodes += c1
            capturedNodes += c2
        }
        assertTrue(capturedNodes.isEmpty())
    }

    @Test
    fun `whenChildrenAttached is invoked multiple times for each pair of children`() {
        val child11 = add(Configuration.Child1(id = 0))
        val child12 = add(Configuration.Child1(id = 1))
        val child21 = add(Configuration.Child2(id = 0))
        val child22 = add(Configuration.Child2(id = 1))
        val capturedNodes = ArrayList<Pair<Node<*>, Node<*>>>()
        root.whenChildrenAttached<Child1, Child2> { _, child1, child2 ->
            capturedNodes += child1 to child2
        }
        assertEquals(
            listOf(child11 to child21, child11 to child22, child12 to child21, child12 to child22),
            capturedNodes
        )
    }

    @Test
    fun `whenChildrenAttached is invoked properly for same class connections`() {
        val child1 = add(Configuration.Child1(id = 0))
        val child2 = add(Configuration.Child1(id = 1))
        val child3 = add(Configuration.Child1(id = 2))
        val capturedNodes = ArrayList<Pair<Node<*>, Node<*>>>()
        root.whenChildrenAttached<Child1, Child1> { _, c1, c2 ->
            capturedNodes += c1 to c2
        }
        assertEquals(
            listOf(child1 to child2, child1 to child3, child2 to child3),
            capturedNodes
        )
    }

    // endregion

    // region Callback lifecycle checks

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

    // endregion

    // region Setup

    private fun add(key: Configuration): Node<*> {
        root.routing.add(key)
        return (root.children.value.values.find { it.key.routing == key } as ChildEntry.Eager).node
    }

    sealed class Configuration {
        data class Child1(val id: Int = 0) : Configuration()
        data class Child2(val id: Int = 0) : Configuration()
        data class Child3(val id: Int = 0) : Configuration()
    }

    class Root(
        val routing: TestRoutingSource<Configuration> = TestRoutingSource(),
    ) : Node<Configuration>(
        savedStateMap = null,
        routingSource = routing,
        childMode = ChildEntry.ChildMode.EAGER,
    ) {
        override fun resolve(routing: Configuration, savedStateMap: SavedStateMap?): Node<*> =
            when (routing) {
                is Configuration.Child1 -> Child1()
                is Configuration.Child2 -> Child2()
                is Configuration.Child3 -> Child3()
            }

        @Composable
        override fun View() {
        }
    }

    class Child1 : EmptyLeafNode()
    class Child2 : EmptyLeafNode()
    class Child3 : EmptyLeafNode()

    abstract class EmptyLeafNode : LeafNode(savedStateMap = null) {
        @Composable
        override fun View() {
        }
    }

    // endregion
}
