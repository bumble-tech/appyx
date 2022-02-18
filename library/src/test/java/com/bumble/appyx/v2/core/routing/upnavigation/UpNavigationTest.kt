package com.bumble.appyx.v2.core.routing.upnavigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.children.ChildEntry
import com.bumble.appyx.v2.core.children.nodeOrNull
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.node.build
import com.bumble.appyx.v2.core.plugin.StubUpNavigationHandler
import com.bumble.appyx.v2.core.plugin.UpNavigationHandler
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.push
import com.bumble.appyx.v2.core.testutils.MainDispatcherRule
import com.bumble.appyx.v2.core.testutils.TestIntegrationPoint
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UpNavigationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fallbackStub = StubFallbackUpNavigationHandler()
    private val integrationPoint = TestIntegrationPoint(fallbackStub)

    // region Child

    @Test
    fun `fallback up navigation is invoked when no plugin provided`() {
        val child = Child(id = "").build()
        child.integrationPoint = integrationPoint

        child.navigateUp()

        fallbackStub.assertInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin intercepts it`() {
        val stub = StubUpNavigationHandler()
        val child = Child(id = "", upNavigationHandler = stub).build()
        child.integrationPoint = integrationPoint

        child.navigateUp()

        stub.assertInvoked()
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is not intercepted when plugin does not intercept it`() {
        val stub = StubUpNavigationHandler(stub = false)
        val child = Child(id = "", upNavigationHandler = stub).build()
        child.integrationPoint = integrationPoint

        child.navigateUp()

        stub.assertInvoked()
        fallbackStub.assertInvoked()
    }

    // endregion

    // region Parent

    @Test
    fun `fallback up navigation is invoked when routing can't go up`() {
        val parent = Parent().build()
        parent.integrationPoint = integrationPoint

        parent.navigateUp()

        fallbackStub.assertInvoked()
    }

    @Test
    fun `up navigation is intercepted by routing source`() {
        val parent = Parent().build()
        parent.backStack.push(Parent.Configuration(id = "1"))
        parent.integrationPoint = integrationPoint

        parent.navigateUp()

        assertEquals(1, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin intercepts it before routing source`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub).build()
        parent.backStack.push(Parent.Configuration(id = "1"))
        parent.integrationPoint = integrationPoint

        parent.navigateUp()

        stub.assertInvoked()
        assertEquals(2, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin does not intercept it before routing source`() {
        val stub = StubUpNavigationHandler(stub = false)
        val parent = Parent(upNavigationHandler = stub).build()
        parent.backStack.push(Parent.Configuration(id = "1"))
        parent.integrationPoint = integrationPoint

        parent.navigateUp()

        stub.assertInvoked()
        assertEquals(1, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `child invokes parent up navigation logic`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub).build()
        parent.backStack.push(Parent.Configuration(id = "1"))

        val child1 = parent.children.value.values.find { (it.nodeOrNull as Child).id == "1" }
        requireNotNull(child1?.nodeOrNull).navigateUp()

        stub.assertInvoked()
    }

    @Test
    fun `up navigation is intercepted by child plugin before parents one`() {
        val parentStub = StubUpNavigationHandler()
        val childStub = StubUpNavigationHandler()
        val parent = Parent(
            upNavigationHandler = parentStub,
            childUpNavigationHandler = childStub,
        ).build()

        val child = parent.children.value.values.first()
        requireNotNull(child.nodeOrNull).navigateUp()

        childStub.assertInvoked()
        parentStub.assertNotInvoked()
    }

    // endregion

    // region Setup

    class Parent(
        buildContext: BuildContext = BuildContext.root(null),
        val backStack: BackStack<Configuration> = BackStack(
            initialElement = Configuration("0"),
            savedStateMap = buildContext.savedStateMap,
        ),
        upNavigationHandler: UpNavigationHandler? = null,
        private val childUpNavigationHandler: UpNavigationHandler? = null,
    ) : ParentNode<Parent.Configuration>(
        buildContext = buildContext,
        routingSource = backStack,
        childMode = ChildEntry.ChildMode.EAGER,
        plugins = upNavigationHandler?.let { listOf(it) } ?: emptyList(),
    ) {
        data class Configuration(val id: String)

        init {
            manageTransitionsInTest()
        }

        override fun resolve(routing: Configuration, buildContext: BuildContext): Node =
            Child(routing.id, buildContext, childUpNavigationHandler)

        @Composable
        override fun View(modifier: Modifier) {
        }
    }

    class Child(
        val id: String,
        buildContext: BuildContext = BuildContext.root(null),
        upNavigationHandler: UpNavigationHandler? = null,
    ) : Node(
        buildContext = buildContext,
        plugins = upNavigationHandler?.let { listOf(it) } ?: emptyList(),
    ) {
        @Composable
        override fun View(modifier: Modifier) {
        }
    }
    // endregion

}
