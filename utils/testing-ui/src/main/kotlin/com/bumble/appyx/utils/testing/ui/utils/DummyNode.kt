package com.bumble.appyx.utils.testing.ui.utils

import com.bumble.appyx.interactions.core.model.EmptyAppyxComponent
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node

class DummyNode<InteractionTarget : Any> : Node<InteractionTarget>(
    appyxComponent = EmptyAppyxComponent(),
    nodeContext = NodeContext.root(savedStateMap = null)
) {
    override fun buildChildNode(navTarget: InteractionTarget, nodeContext: NodeContext) = node(nodeContext) { }
}
