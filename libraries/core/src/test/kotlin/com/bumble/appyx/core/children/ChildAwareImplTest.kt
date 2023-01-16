package com.bumble.appyx.core.children

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Put here specific cases which should not be tested for both before/after registration
class ChildAwareImplTest : ChildAwareTestBase() {

    // region Single

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for destroyed node`() {
        root = Root().build()
        var capturedLifecycle: Lifecycle? = null
        root.whenChildAttachedTest(Child1::class) { lifecycle, _ ->
            capturedLifecycle = lifecycle
        }
        val navKey = NavKey<Configuration>(Configuration.Child1())
        add(navKey)
        root.testNavModel.destroy(navKey)
        assertEquals(Lifecycle.State.DESTROYED, capturedLifecycle?.currentState)
    }

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for suspended node`() {
        root = Root(mode = ChildEntry.KeepMode.SUSPEND).build()
        var capturedLifecycle: Lifecycle? = null
        root.whenChildAttachedTest(Child1::class) { lifecycle, _ ->
            capturedLifecycle = lifecycle
        }
        val navKey = NavKey<Configuration>(Configuration.Child1())
        add(navKey)
        root.testNavModel.changeState(visible = false, navKey)
        assertEquals(Lifecycle.State.DESTROYED, capturedLifecycle?.currentState)
    }

    @Test
    fun `whenChildAttached is invoked for unsuspended node`() {
        root = Root(mode = ChildEntry.KeepMode.SUSPEND).build()
        var capturedNode: Node? = null
        root.whenChildAttachedTest(Child1::class) { _, child ->
            capturedNode = child
        }
        val navKey = NavKey<Configuration>(Configuration.Child1())
        root.testNavModel.add(visible = false, navKey)
        assertNull(capturedNode)

        root.testNavModel.changeState(visible = true, navKey)
        val node = root.children(navKey).firstOrNull()
        assertEquals(node, capturedNode)
    }

    // endregion

    // region Double

    @Test
    fun `whenChildrenAttached lifecycle is destroyed properly for destroyed node`() {
        root = Root().build()
        var capturedLifecycle: Lifecycle? = null
        root.whenChildrenAttachedTest(Child1::class, Child2::class) { lifecycle, _, _ ->
            capturedLifecycle = lifecycle
        }
        val navKey1 = NavKey<Configuration>(Configuration.Child1(id = 0))
        val navKey2 = NavKey<Configuration>(Configuration.Child2(id = 1))
        add(navKey1, navKey2)
        root.testNavModel.destroy(navKey1, navKey2)
        assertEquals(Lifecycle.State.DESTROYED, capturedLifecycle?.currentState)
    }

    @Test
    fun `whenChildrenAttached lifecycle is destroyed properly for suspended node`() {
        root = Root(mode = ChildEntry.KeepMode.SUSPEND).build()
        var capturedLifecycle: Lifecycle? = null
        root.whenChildrenAttachedTest(Child1::class, Child2::class) { lifecycle, _, _ ->
            capturedLifecycle = lifecycle
        }
        val navKey1 = NavKey<Configuration>(Configuration.Child1(id = 0))
        val navKey2 = NavKey<Configuration>(Configuration.Child2(id = 1))
        add(navKey1, navKey2)
        root.testNavModel.changeState(visible = false, navKey1, navKey2)
        assertEquals(Lifecycle.State.DESTROYED, capturedLifecycle?.currentState)
    }

    @Test
    fun `whenChildrenAttached is invoked for unsuspended node`() {
        root = Root(mode = ChildEntry.KeepMode.SUSPEND).build()
        val capturedNodes = HashSet<Node>()
        root.whenChildrenAttachedTest(Child1::class, Child2::class) { _, c1, c2 ->
            capturedNodes += c1
            capturedNodes += c2
        }
        val navKey1 = NavKey<Configuration>(Configuration.Child1(id = 0))
        val navKey2 = NavKey<Configuration>(Configuration.Child2(id = 1))
        root.testNavModel.add(visible = false, navKey1)
        root.testNavModel.add(visible = true, navKey2)
        root.testNavModel.changeState(visible = true, navKey1)
        val nodes = root.children(navKey1, navKey2).toSet()
        assertEquals(nodes, capturedNodes)
    }

    // endregion

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
