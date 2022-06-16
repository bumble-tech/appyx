package com.bumble.appyx.core.builder

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

abstract class SimpleBuilder {
    abstract fun build(buildContext: BuildContext): Node
}
