package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.EmptyNodeView
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.NodeView

class NodeStub(
    nodeContext: NodeContext = NodeContext.root(savedStateMap = null),
    plugins: List<Plugin> = emptyList(),
    view: NodeView = EmptyNodeView,
) : Node(
    nodeContext = nodeContext,
    plugins = plugins,
    view = view,
)
