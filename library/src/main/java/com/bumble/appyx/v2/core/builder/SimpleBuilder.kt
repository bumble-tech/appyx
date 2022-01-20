package com.bumble.appyx.v2.core.builder

import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

abstract class SimpleBuilder {
    abstract fun build(buildContext: BuildContext): Node
}
