package com.bumble.appyx.sandbox.client.customisations

import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

class ViewCustomisationExampleBuilder : SimpleBuilder() {
    override fun build(buildContext: BuildContext): Node {
        val viewFactory =
            buildContext.getOrDefault(ViewCustomisationExampleNode.Customisations()).viewFactory

        return ViewCustomisationExampleNode(
            buildContext = buildContext,
            view = viewFactory.invoke()
        )
    }
}
