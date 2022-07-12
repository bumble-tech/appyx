package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.NodeView

class MviCoreLeafNode(
    buildContext: BuildContext,
    view: NodeView,
    interactor: MviCoreLeafInteractor
) : Node(
    buildContext = buildContext,
    view = view,
    plugins = listOf(interactor)
)
