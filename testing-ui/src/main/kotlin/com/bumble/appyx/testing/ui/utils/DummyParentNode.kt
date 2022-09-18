package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node

class DummyParentNode<Routing : Any> : ParentNode<Routing>(
    navModel = DummyNavModel<Routing, Unit>(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun resolve(navTarget: Routing, buildContext: BuildContext) = node(buildContext) { }
}
