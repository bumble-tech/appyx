package com.bumble.appyx.utils.testing.ui.utils

import com.bumble.appyx.interactions.core.model.EmptyAppyxComponent
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node

class DummyParentNode<InteractionTarget : Any> : ParentNode<InteractionTarget>(
    appyxComponent = EmptyAppyxComponent(),
    nodeContext = NodeContext.root(savedStateMap = null)
) {
    override fun buildChildNode(navTarget: InteractionTarget, nodeContext: NodeContext) = node(nodeContext) { }
}
