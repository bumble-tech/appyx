package com.bumble.appyx.v2.client.interactorusage

import com.bumble.appyx.v2.core.builder.SimpleBuilder
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

class InteractorNodeBuilder : SimpleBuilder() {
    override fun build(buildContext: BuildContext): Node {
        val interactor = InteractorExample()
        return InteractorExampleNode(buildContext = buildContext, interactor = interactor)
    }
}
