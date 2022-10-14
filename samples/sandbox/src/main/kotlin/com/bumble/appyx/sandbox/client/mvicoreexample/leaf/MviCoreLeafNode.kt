package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.core.plugin.Plugin

class MviCoreLeafNode(
    buildContext: BuildContext,
    view: NodeView,
    plugins: List<Plugin>
) : Node(
    buildContext = buildContext,
    view = view,
    plugins = plugins
)
