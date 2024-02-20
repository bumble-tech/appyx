package com.bumble.appyx.utils.testing.ui.utils

import com.bumble.appyx.interactions.model.EmptyAppyxComponent
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node

class DummyNode<InteractionTarget : Any> : Node<InteractionTarget>(
    appyxComponent = EmptyAppyxComponent(),
    nodeContext = NodeContext.root(savedStateMap = null)
) {
    override fun buildChildNode(navTarget: InteractionTarget, nodeContext: NodeContext) = node(nodeContext) { }
}
