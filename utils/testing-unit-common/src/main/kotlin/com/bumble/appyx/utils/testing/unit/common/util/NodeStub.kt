package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.EmptyNodeView
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.node.NodeView

class NodeStub(
    nodeContext: NodeContext = NodeContext.root(savedStateMap = null),
    plugins: List<Plugin> = emptyList(),
    view: NodeView = EmptyNodeView,
) : AbstractNode(
    nodeContext = nodeContext,
    plugins = plugins,
    view = view,
)
