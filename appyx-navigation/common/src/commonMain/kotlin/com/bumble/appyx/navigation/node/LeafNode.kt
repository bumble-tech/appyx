package com.bumble.appyx.navigation.node

import com.bumble.appyx.interactions.model.EmptyAppyxComponent
import com.bumble.appyx.interactions.plugin.Plugin
import com.bumble.appyx.navigation.modality.NodeContext

open class LeafNode(
    nodeContext: NodeContext,
    view: NodeView = EmptyNodeView(),
    plugins: List<Plugin> = listOf(),
) : Node<Nothing>(
    appyxComponent = EmptyAppyxComponent(),
    nodeContext = nodeContext,
    view = view,
    plugins = plugins,
) {
    override fun buildChildNode(navTarget: Nothing, nodeContext: NodeContext): Node<*> {
        error("A leaf node should never have to build a child node")
    }
}
