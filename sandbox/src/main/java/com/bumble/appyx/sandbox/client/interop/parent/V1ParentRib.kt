package com.bumble.appyx.sandbox.client.interop.parent

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentViewImpl.Factory

interface V1ParentRib : Rib {

    class Customisation(
        val viewFactory: Factory = Factory(),
        val name: String = "defaultName"
    ) : RibCustomisation
}

class V1ParentNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<V1ParentView>?,
    plugins: List<Plugin> = emptyList(),
) : Node<V1ParentView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), V1ParentRib
