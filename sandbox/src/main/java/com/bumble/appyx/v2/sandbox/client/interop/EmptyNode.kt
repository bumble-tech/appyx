package com.bumble.appyx.v2.sandbox.client.interop

import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.rx2.workflows.RxWorkflowNode

class EmptyNode internal constructor(
    buildParams: BuildParams<*>,
    plugins: List<Plugin> = emptyList(),
) : RxWorkflowNode<RibView>(
    buildParams = buildParams,
    viewFactory = null,
    plugins = plugins
), V1Rib
