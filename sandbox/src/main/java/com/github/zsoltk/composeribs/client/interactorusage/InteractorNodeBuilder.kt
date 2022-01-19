package com.github.zsoltk.composeribs.client.interactorusage

import com.github.zsoltk.composeribs.core.builder.SimpleBuilder
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node

class InteractorNodeBuilder : SimpleBuilder() {
    override fun build(buildContext: BuildContext): Node {
        val interactor = InteractorExample()
        return InteractorExampleNode(buildContext = buildContext, interactor = interactor)
    }
}
