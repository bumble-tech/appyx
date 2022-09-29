package com.bumble.appyx.core.navigation.upnavigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.plugin.StubUpNavigationHandler
import com.bumble.appyx.core.plugin.UpNavigationHandler
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.util.TestIntegrationPoint
import com.bumble.appyx.testing.unit.common.util.TestUpNavigationHandler
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class UpNavigationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testUpNavigationHandler = TestUpNavigationHandler()
    private val integrationPoint = TestIntegrationPoint(testUpNavigationHandler)

    // region Child

    @Test
    fun `integrationPoint up navigation is invoked without parent`() {
        val child = Child().build()
        child.integrationPoint = integrationPoint

        child.navigateUp()

        testUpNavigationHandler.assertInvoked()
    }

    @Test
    fun `up navigation is not intercepted by caller plugin`() {
        val stub = StubUpNavigationHandler()
        val child = Child(upNavigationHandler = stub).build()
        child.integrationPoint = integrationPoint

        child.navigateUp()

        stub.assertNotInvoked()
        testUpNavigationHandler.assertInvoked()
    }

    // endregion

    // region Parent

    @Test
    fun `integrationPoint up navigation is invoked when parent can't handle child up navigation`() {
        val parent = Parent().build()
        parent.integrationPoint = integrationPoint

        val child = parent.findChild<Child>(CHILD_ID)
        child.navigateUp()

        assertEquals(1, parent.children.value.size)
        testUpNavigationHandler.assertInvoked()
    }

    @Test
    fun `parent plugin handles child up navigation`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub).build()
        parent.integrationPoint = integrationPoint

        val child = parent.findChild<Child>(CHILD_ID)
        child.navigateUp()

        stub.assertInvoked()
        testUpNavigationHandler.assertNotInvoked()
    }

    @Test
    fun `parent router handles child up navigation without plugin`() {
        val parent = Parent().build()
        parent.integrationPoint = integrationPoint

        val newChildId = UUID.randomUUID()
        parent.backStack.push(Parent.Configuration(newChildId))
        val child = parent.findChild<Child>(newChildId)
        child.navigateUp()

        assertEquals(1, parent.children.value.size)
        testUpNavigationHandler.assertNotInvoked()
    }

    @Test
    fun `parent plugin handles child up navigation instead of router`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub).build()
        parent.integrationPoint = integrationPoint

        val newChildId = UUID.randomUUID()
        parent.backStack.push(Parent.Configuration(newChildId))
        val child = parent.findChild<Child>(newChildId)
        child.navigateUp()

        assertEquals(2, parent.children.value.size)
        stub.assertInvoked()
        testUpNavigationHandler.assertNotInvoked()
    }

    // endregion

    // region Setup

    interface NodeWithId {
        val uuid: UUID
    }

    class Parent(
        override val uuid: UUID = PARENT_ID,
        buildContext: BuildContext = BuildContext.root(null),
        val backStack: BackStack<Configuration> = BackStack(
            initialElement = Configuration(CHILD_ID),
            savedStateMap = buildContext.savedStateMap,
        ),
        upNavigationHandler: UpNavigationHandler? = null,
        private val childUpNavigationHandler: UpNavigationHandler? = null,
    ) : ParentNode<Parent.Configuration>(
        buildContext = buildContext,
        navModel = backStack,
        plugins = upNavigationHandler?.let { listOf(it) } ?: emptyList(),
    ), NodeWithId {
        data class Configuration(val id: UUID)

        init {
            manageTransitionsInTest()
        }

        override fun resolve(navTarget: Configuration, buildContext: BuildContext): Node =
            Child(navTarget.id, buildContext, childUpNavigationHandler)

        @Composable
        override fun View(modifier: Modifier) {
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : NodeWithId> findChild(id: UUID): T =
            children.value.values.find { it.key.navTarget.id == id }?.nodeOrNull as T

    }

    class Child(
        override val uuid: UUID = CHILD_ID,
        buildContext: BuildContext = BuildContext.root(null),
        upNavigationHandler: UpNavigationHandler? = null,
    ) : Node(
        buildContext = buildContext,
        plugins = upNavigationHandler?.let { listOf(it) } ?: emptyList(),
    ), NodeWithId {
        @Composable
        override fun View(modifier: Modifier) {
        }
    }

    companion object {
        private val PARENT_ID = UUID.randomUUID()
        private val CHILD_ID = UUID.randomUUID()
    }

    // endregion

}
