package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.interactions.core.model.EmptyInteractionModel
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node

class DummyParentNode<InteractionTarget : Any> : ParentNode<InteractionTarget>(
    interactionModel = EmptyInteractionModel(),
    buildContext = BuildContext.root(savedStateMap = null)
) {
    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext) = node(buildContext) { }
}
