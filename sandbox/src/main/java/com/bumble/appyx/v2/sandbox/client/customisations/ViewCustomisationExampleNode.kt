package com.bumble.appyx.v2.sandbox.client.customisations

import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.NodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import com.bumble.appyx.v2.core.plugin.Plugin

class ViewCustomisationExampleNode(
    private val view: NodeView,
    buildContext: BuildContext,
    plugins: List<Plugin>
) : Node(
    buildContext = buildContext,
    plugins = plugins
), NodeView by view {

    class Customisations(
        val viewFactory: ViewFactory<ViewCustomisationExampleNode> = ViewFactory {
            ViewCustomisationExampleDefaultView()
        }
    ) : NodeCustomisation
}
