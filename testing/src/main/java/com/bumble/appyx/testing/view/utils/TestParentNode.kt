package com.bumble.appyx.testing.view.utils

import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.node.node

class TestParentNode<Routing : Any> : ParentNode<Routing>(
    routingSource = DummyRoutingSource<Routing, Unit>(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun resolve(routing: Routing, buildContext: BuildContext) = node(buildContext) { }
}
