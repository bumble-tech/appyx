package com.bumble.appyx.core.builder

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

abstract class Builder<P> {
    abstract fun build(buildContext: BuildContext, payload: P): Node
}
