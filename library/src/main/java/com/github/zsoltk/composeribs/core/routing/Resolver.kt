package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.modality.BuildContext

interface Resolver<T> {
    fun resolve(routing: T, buildContext: BuildContext): Node
}
