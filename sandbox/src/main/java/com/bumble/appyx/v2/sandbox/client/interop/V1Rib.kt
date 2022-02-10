package com.bumble.appyx.v2.sandbox.client.interop

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.rx2.workflows.RxWorkflowNode
import com.bumble.appyx.v2.sandbox.client.interop.V1ViewImpl.Factory

interface V1Rib : Rib {

    class Customisation(val viewFactory: Factory = Factory()) : RibCustomisation
}

class NodeV1 internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<V1View>?,
    plugins: List<Plugin> = emptyList(),
) : RxWorkflowNode<V1View>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), V1Rib
