package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.EmptyNodeView
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.NodeView

class NodeStub(
    buildContext: BuildContext = BuildContext.root(savedStateMap = null),
    plugins: List<Plugin> = emptyList(),
    view: NodeView = EmptyNodeView,
) : Node(
    buildContext = buildContext,
    plugins = plugins,
    view = view,
)
