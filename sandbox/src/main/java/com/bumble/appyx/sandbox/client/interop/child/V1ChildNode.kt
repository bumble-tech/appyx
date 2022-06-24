package com.bumble.appyx.sandbox.client.interop.child

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.view.RibView

class V1ChildNode internal constructor(buildParams: BuildParams<*>) : Node<RibView>(
    buildParams = buildParams,
    viewFactory = V1ChildViewImpl.Factory().invoke(null),
    plugins = emptyList()
)
