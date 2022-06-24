package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node

class DummyParentNode<Routing : Any> : ParentNode<Routing>(
    routingSource = DummyRoutingSource<Routing, Unit>(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun resolve(routing: Routing, buildContext: BuildContext) = node(buildContext) { }
}
