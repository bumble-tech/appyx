package com.bumble.appyx.sandbox.client.mvicoreexample

import android.os.Parcelable
import com.bumble.appyx.connectable.rx2.Connectable
import com.bumble.appyx.connectable.rx2.NodeConnector
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import kotlinx.parcelize.Parcelize
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Input as Child1Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode1.Output as Child1Output
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Input as Child2Input
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreChildNode2.Output as Child2Output

class MviCoreExampleNode(
    view: ParentNodeView<Routing>,
    buildContext: BuildContext,
    plugins: List<Plugin>,
    backStack: BackStack<Routing>,
    connector: NodeConnector<Input, Nothing> = NodeConnector(),
    private val child1Connector: NodeConnector<Child1Input, Child1Output> = NodeConnector(),
    private val child2Connector: NodeConnector<Child2Input, Child2Output> = NodeConnector(),
) : ParentNode<Routing>(
    view = view,
    navModel = backStack,
    buildContext = buildContext,
    plugins = plugins
), Connectable<Input, Nothing> by connector {

    sealed class Input {
        object ExampleInput : Input()
    }

    sealed class Routing : Parcelable {
        @Parcelize
        object Child1 : Routing()

        @Parcelize
        object Child2 : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Child1 -> MviCoreChildNode1(
                buildContext = buildContext,
                connector = child1Connector,
            )
            is Routing.Child2 -> MviCoreChildNode2(
                buildContext = buildContext,
                connector = child2Connector,
            )
        }
}
