package com.bumble.appyx.core.routing

import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.modality.BuildContext

interface Resolver<Routing> {
    fun resolve(routing: Routing, buildContext: BuildContext): Node
}
