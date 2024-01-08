package com.bumble.appyx.utils.testing.ui.utils

import com.bumble.appyx.interactions.core.model.EmptyAppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node

class DummyParentNode<InteractionTarget : Any> : ParentNode<InteractionTarget>(
    appyxComponent = EmptyAppyxComponent(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun buildChildNode(reference: InteractionTarget, buildContext: BuildContext) = node(buildContext) { }
}
