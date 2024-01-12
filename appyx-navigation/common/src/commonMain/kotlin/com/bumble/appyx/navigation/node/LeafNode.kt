package com.bumble.appyx.navigation.node

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.interactions.permanent.PermanentAppyxComponent
import com.bumble.appyx.navigation.modality.NodeContext

open class LeafNode(
    nodeContext: NodeContext,
    view: ParentNodeView<Nothing> = EmptyParentNodeView(),
    plugins: List<Plugin> = listOf(),
) : ParentNode<Nothing>(
    appyxComponent = PermanentAppyxComponent(
        savedStateMap = nodeContext.savedStateMap
    ),
    nodeContext = nodeContext,
    view = view,
    plugins = plugins,
) {
    override fun buildChildNode(navTarget: Nothing, nodeContext: NodeContext): AbstractNode {
        error("A leaf node should never have to build a child node")
    }
}
