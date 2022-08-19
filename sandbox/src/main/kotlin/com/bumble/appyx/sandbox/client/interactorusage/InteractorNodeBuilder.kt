package com.bumble.appyx.sandbox.client.interactorusage

import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

class InteractorNodeBuilder : SimpleBuilder() {
    override fun build(buildContext: BuildContext): Node {
        val interactor = InteractorExample()
        return InteractorExampleNode(buildContext = buildContext, interactor = interactor)
    }
}
