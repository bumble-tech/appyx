package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node

class DummyParentNode<NavTarget : Any> : ParentNode<NavTarget>(
    navModel = DummyNavModel<NavTarget, Unit>(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext) = node(buildContext) { }
}
