package com.bumble.appyx.sandbox.client.interop.child

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.view.RibView

class RibsChildNode internal constructor(buildParams: BuildParams<*>) : Node<RibView>(
    buildParams = buildParams,
    viewFactory = RibsChildViewImpl.Factory().invoke(null),
    plugins = emptyList()
)
