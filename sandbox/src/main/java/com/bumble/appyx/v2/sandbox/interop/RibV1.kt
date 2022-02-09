package com.bumble.appyx.v2.sandbox.interop

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.rx2.workflows.RxWorkflowNode

interface RibV1 : Rib {

    class Customisation(
        val viewFactory: ViewV1Impl.Factory = ViewV1Impl.Factory()
    ) : RibCustomisation
}

class NodeV1 internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<ViewV1>?,
    plugins: List<Plugin> = emptyList(),
) : RxWorkflowNode<ViewV1>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), RibV1