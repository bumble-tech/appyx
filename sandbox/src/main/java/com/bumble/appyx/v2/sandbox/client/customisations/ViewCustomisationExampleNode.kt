package com.bumble.appyx.v2.sandbox.client.customisations

import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ViewFactory

class ViewCustomisationExampleNode(
    buildContext: BuildContext,
    viewFactory: ViewFactory<ViewCustomisationExampleNode>,
) : Node(
    view = viewFactory.invoke(),
    buildContext = buildContext
) {

    class Customisations(
        val viewFactory: ViewFactory<ViewCustomisationExampleNode> = ViewFactory {
            ViewCustomisationExampleDefaultView()
        }
    ) : NodeCustomisation
}
