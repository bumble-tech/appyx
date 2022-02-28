package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.Plugin
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import kotlinx.parcelize.Parcelize

class MviCoreExampleNode(
    private val view: MviCoreView,
    buildContext: BuildContext,
    plugins: List<Plugin> = listOf(),
    internal val backStack: BackStack<Routing>,
) : ParentNode<Routing>(
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

    @Composable
    override fun View(modifier: Modifier) {
        view.MviCoreView(modifier = modifier, node = this)
    }
}
