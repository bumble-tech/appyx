package com.github.zsoltk.composeribs.core.builder

import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node

abstract class Builder<P> {
    abstract fun build(buildContext: BuildContext, payload: P): Node
}
