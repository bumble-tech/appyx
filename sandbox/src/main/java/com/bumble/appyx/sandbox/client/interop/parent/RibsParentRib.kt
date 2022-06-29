package com.bumble.appyx.sandbox.client.interop.parent

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.ViewFactory
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentViewImpl.Factory

interface RibsParentRib : Rib {

    class Customisation(
        val viewFactory: Factory = Factory(),
        val name: String = "defaultName"
    ) : RibCustomisation
}

class RibsParentNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ViewFactory<RibsParentView>?,
    plugins: List<Plugin> = emptyList(),
) : Node<RibsParentView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), RibsParentRib
