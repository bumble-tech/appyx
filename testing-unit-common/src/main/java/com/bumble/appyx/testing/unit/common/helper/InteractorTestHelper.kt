package com.bumble.appyx.testing.unit.common.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.testing.unit.common.util.DummyRoutingSource

fun <N : Node> Interactor<N>.interactorTestHelper(
    routingSource: RoutingSource<*, *> = DummyRoutingSource<Any, Any>(),
    childBuilder: (BuildContext) -> Node = { InteractorTestHelper.EmptyLeafNode(it) }
) =
    InteractorTestHelper(
        interactor = this,
        routingSource = routingSource,
        childBuilder = childBuilder
    )

class InteractorTestHelper<N : Node>(
    private val interactor: Interactor<N>,
    private val routingSource: RoutingSource<*, *> = DummyRoutingSource<Any, Any>(),
    private val childBuilder: (BuildContext) -> Node = { EmptyLeafNode(it) }
) {
    private val node = object : ParentNode<Any>(
        buildContext = BuildContext.root(savedStateMap = null),
        routingSource = routingSource as BaseRoutingSource<Any, *>,
        plugins = listOf(interactor),
        childMode = ChildEntry.ChildMode.EAGER
    ) {
        override fun resolve(routing: Any, buildContext: BuildContext): Node =
            childBuilder(buildContext)
    }

    private val nodeTestHelper = node.nodeTestHelper()

    fun moveTo(state: Lifecycle.State) {
        nodeTestHelper.moveTo(state)
    }

    /**
     * moves the Node to the desired state and then returns to the original state if possible
     */
    fun moveToStateAndCheck(state: Lifecycle.State, block: (Node) -> Unit) {
        nodeTestHelper.moveToStateAndCheck(state, block)
    }

    internal class EmptyLeafNode(
        buildContext: BuildContext
    ) : Node(buildContext = buildContext) {
        @Composable
        override fun View(modifier: Modifier) {
        }
    }
}
