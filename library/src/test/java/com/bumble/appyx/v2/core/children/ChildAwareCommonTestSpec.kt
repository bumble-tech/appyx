package com.bumble.appyx.v2.core.children

import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.routing.RoutingKey
import org.junit.Assert.*
import org.junit.Test
import kotlin.reflect.KClass

// Common set of tests for callback verification despite registration time
interface ChildAwareCommonTestSpec {

    val registerBefore: Boolean

    fun add(vararg key: RoutingKey<ChildAwareTestBase.Configuration>): List<Node>

    fun <T : Node> whenChildAttached(klass: KClass<T>, callback: ChildCallback<T>)

    private inline fun <reified T : Node> whenChildAttached(
        noinline callback: ChildCallback<T>
    ) {
        whenChildAttached(T::class, callback)
    }

    fun <T1 : Node, T2 : Node> whenChildrenAttached(
        klass1: KClass<T1>,
        klass2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    )

    private inline fun <reified T1 : Node, reified T2 : Node> whenChildrenAttached(
        noinline callback: ChildrenCallback<T1, T2>
    ) {
        whenChildrenAttached(T1::class, T2::class, callback)
    }

    private fun <T> withRegistration(register: () -> Unit, block: () -> T): T {
        if (registerBefore) register()
        val result = block()
        if (!registerBefore) register()
        return result
    }

    // region Single

    @Test
    fun `whenChildAttached is invoked`() {
        var capturedNode: Node? = null
        val children = withRegistration(register = {
            whenChildAttached<ChildAwareTestBase.Child1> { _, child ->
                capturedNode = child
            }
        }) {
            add(RoutingKey(ChildAwareTestBase.Configuration.Child1()))
        }
        assertEquals(children[0], capturedNode)
    }

    @Test
    fun `every whenChildAttached is invoked`() {
        var capturedNode1: Node? = null
        var capturedNode2: Node? = null

        val children = withRegistration(register = {
            whenChildAttached<ChildAwareTestBase.Child1> { _, child ->
                capturedNode1 = child
            }
            whenChildAttached<ChildAwareTestBase.Child1> { _, child ->
                capturedNode2 = child
            }
        }) {
            add(RoutingKey(ChildAwareTestBase.Configuration.Child1()))
        }

        assertEquals(children[0], capturedNode1)
        assertEquals(children[0], capturedNode2)
    }

    @Test
    fun `whenChildAttached is invoked multiple times for each instance`() {
        val capturedNodes = HashSet<ChildAwareTestBase.Child1>()
        val children = withRegistration(register = {
            whenChildAttached<ChildAwareTestBase.Child1> { _, child ->
                capturedNodes += child
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 0)),
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 1)),
            )
        }
        assertEquals(setOf(children[0], children[1]), capturedNodes)
    }

    @Test
    fun `whenChildAttached is not invoked for unrelated child`() {
        var capturedNode: Node? = null
        withRegistration(register = {
            whenChildAttached<ChildAwareTestBase.Child1> { _, child ->
                capturedNode = child
            }
        }) {
            add(RoutingKey(ChildAwareTestBase.Configuration.Child2()))
        }
        assertNull(capturedNode)
    }

    // endregion

    // region Double

    @Test
    fun `whenChildrenAttached is invoked`() {
        val capturedNodes = HashSet<Node>()
        val children = withRegistration(register = {
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child2> { _, c1, c2 ->
                capturedNodes += c1
                capturedNodes += c2
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1()),
                RoutingKey(ChildAwareTestBase.Configuration.Child2()),
            )
        }
        assertEquals(setOf(children[0], children[1]), capturedNodes)
    }

    @Test
    fun `every whenChildrenAttached is invoked`() {
        val capturedNodes1 = mutableSetOf<Node>()
        val capturedNodes2 = mutableSetOf<Node>()
        val children = withRegistration(register = {
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child2> { _, c1, c2 ->
                capturedNodes1.add(c1)
                capturedNodes1.add(c2)
            }
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child2> { _, c1, c2 ->
                capturedNodes2.add(c1)
                capturedNodes2.add(c2)
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1()),
                RoutingKey(ChildAwareTestBase.Configuration.Child2()),
            )
        }
        assertEquals(setOf(children[0], children[1]), capturedNodes1)
        assertEquals(setOf(children[0], children[1]), capturedNodes2)
    }

    @Test
    fun `whenChildrenAttached is invoked multiple times for each instance`() {
        val capturedNodes = mutableSetOf<Set<Node>>()

        val children = withRegistration(register = {
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child2> { _, c1, c2 ->
                capturedNodes.add(setOf(c1, c2))
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 0)),
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 1)),
                RoutingKey(ChildAwareTestBase.Configuration.Child2(id = 0)),
                RoutingKey(ChildAwareTestBase.Configuration.Child2(id = 1)),
            )
        }

        assertTrue(capturedNodes.contains(setOf(children[0], children[2])))
        assertTrue(capturedNodes.contains(setOf(children[0], children[3])))
        assertTrue(capturedNodes.contains(setOf(children[1], children[2])))
        assertTrue(capturedNodes.contains(setOf(children[1], children[3])))
    }

    @Test
    fun `whenChildrenAttached is not invoked for unrelated children`() {
        val capturedNodes = ArrayList<Node>()
        withRegistration(register = {
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child2> { _, c1, c2 ->
                capturedNodes += c1
                capturedNodes += c2
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1()),
                RoutingKey(ChildAwareTestBase.Configuration.Child3()),
            )
        }
        assertTrue(capturedNodes.isEmpty())
    }

    @Test
    fun `whenChildrenAttached is invoked properly for same class connections`() {
        val capturedNodes = HashSet<Pair<Node, Node>>()
        val children = withRegistration(register = {
            whenChildrenAttached<ChildAwareTestBase.Child1, ChildAwareTestBase.Child1> { _, c1, c2 ->
                capturedNodes += c1 to c2
            }
        }) {
            add(
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 0)),
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 1)),
                RoutingKey(ChildAwareTestBase.Configuration.Child1(id = 2)),
            )
        }
        assertEquals(
            setOf(
                children[0] to children[1],
                children[0] to children[2],
                children[1] to children[2],
            ),
            capturedNodes
        )
    }

    // endregion

}
