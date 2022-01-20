package com.bumble.appyx.v2.core.routing

import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.modality.BuildContext

interface Resolver<Routing> {
    fun resolve(routing: Routing, buildContext: BuildContext): Node
}
