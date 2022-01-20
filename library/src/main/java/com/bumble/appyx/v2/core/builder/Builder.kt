package com.bumble.appyx.v2.core.builder

import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

abstract class Builder<P> {
    abstract fun build(buildContext: BuildContext, payload: P): Node
}
