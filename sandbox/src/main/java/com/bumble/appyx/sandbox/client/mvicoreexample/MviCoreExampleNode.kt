package com.bumble.appyx.sandbox.client.mvicoreexample

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import kotlinx.parcelize.Parcelize

class MviCoreExampleNode(
    view: ParentNodeView<Routing>,
    buildContext: BuildContext,
    plugins: List<Plugin>,
    backStack: BackStack<Routing>,
) : ParentNode<Routing>(
    view = view,
    routingSource = backStack,
    buildContext = buildContext,
    plugins = plugins
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Child1 : Routing()

        @Parcelize
        object Child2 : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Child1 -> MviCoreChildNode1(buildContext)
            is Routing.Child2 -> MviCoreChildNode2(buildContext)
        }
}
