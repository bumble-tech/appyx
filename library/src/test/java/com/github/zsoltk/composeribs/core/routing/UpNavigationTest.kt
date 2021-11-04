package com.github.zsoltk.composeribs.core.routing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.children.nodeOrNull
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.plugin.StubUpNavigationHandler
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UpNavigationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Child

    @Test
    fun `fallback up navigation is invoked when no plugin provided`() {
        val child = Child(id = "")
        val fallbackStub = StubFallbackUpNavigationHandler()
        child.injectFallbackUpNavigationHandler(fallbackStub)

        child.upNavigation()

        fallbackStub.assertInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin intercepts it`() {
        val stub = StubUpNavigationHandler()
        val child = Child(id = "", upNavigationHandler = stub)
        val fallbackStub = StubFallbackUpNavigationHandler()
        child.injectFallbackUpNavigationHandler(fallbackStub)

        child.upNavigation()

        stub.assertInvoked()
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is not intercepted when plugin does not intercept it`() {
        val stub = StubUpNavigationHandler(stub = false)
        val child = Child(id = "", upNavigationHandler = stub)
        val fallbackStub = StubFallbackUpNavigationHandler()
        child.injectFallbackUpNavigationHandler(fallbackStub)

        child.upNavigation()

        stub.assertInvoked()
        fallbackStub.assertInvoked()
    }

    // endregion

    // region Parent

    @Test
    fun `fallback up navigation is invoked when routing can't go up`() {
        val parent = Parent()
        val fallbackStub = StubFallbackUpNavigationHandler()
        parent.injectFallbackUpNavigationHandler(fallbackStub)

        parent.upNavigation()

        fallbackStub.assertInvoked()
    }

    @Test
    fun `up navigation is intercepted by routing source`() {
        val parent = Parent()
        parent.backStack.push(Parent.Configuration(id = "1"))
        val fallbackStub = StubFallbackUpNavigationHandler()
        parent.injectFallbackUpNavigationHandler(fallbackStub)

        parent.upNavigation()

        assertEquals(1, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin intercepts it before routing source`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub)
        parent.backStack.push(Parent.Configuration(id = "1"))
        val fallbackStub = StubFallbackUpNavigationHandler()
        parent.injectFallbackUpNavigationHandler(fallbackStub)

        parent.upNavigation()

        stub.assertInvoked()
        assertEquals(2, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `up navigation is intercepted when plugin does not intercept it before routing source`() {
        val stub = StubUpNavigationHandler(stub = false)
        val parent = Parent(upNavigationHandler = stub)
        parent.backStack.push(Parent.Configuration(id = "1"))
        val fallbackStub = StubFallbackUpNavigationHandler()
        parent.injectFallbackUpNavigationHandler(fallbackStub)

        parent.upNavigation()

        stub.assertInvoked()
        assertEquals(1, parent.children.value.size)
        fallbackStub.assertNotInvoked()
    }

    @Test
    fun `child invokes parent up navigation logic`() {
        val stub = StubUpNavigationHandler()
        val parent = Parent(upNavigationHandler = stub)
        parent.backStack.push(Parent.Configuration(id = "1"))

        val child1 = parent.children.value.values.find { (it.nodeOrNull as Child).id == "1" }
        requireNotNull(child1?.nodeOrNull).upNavigation()

        stub.assertInvoked()
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
            Child(routing.id, buildContext)

        @Composable
        override fun View() {
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
        override fun View() {
        }
    }

    // endregion

}
