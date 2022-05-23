package com.bumble.appyx.v2.sandbox.client.customisations

import com.bumble.appyx.v2.core.builder.SimpleBuilder
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

class ViewCustomisationExampleBuilder : SimpleBuilder() {
    override fun build(buildContext: BuildContext): Node {
        val viewFactory =
            buildContext.getOrDefault(ViewCustomisationExampleNode.Customisations()).viewFactory
        val view = viewFactory.invoke()

        return ViewCustomisationExampleNode(
            buildContext = buildContext,
            view = view,
            plugins = listOf(view)
        )
    }
}
