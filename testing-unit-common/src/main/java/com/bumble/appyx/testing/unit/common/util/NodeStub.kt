package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.EmptyNodeView
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.core.plugin.Plugin

class NodeStub(
    buildContext: BuildContext = BuildContext.root(savedStateMap = null),
    plugins: List<Plugin> = emptyList(),
    view: NodeView = EmptyNodeView,
) : Node(
    buildContext = buildContext,
    plugins = plugins,
    view = view,
)
