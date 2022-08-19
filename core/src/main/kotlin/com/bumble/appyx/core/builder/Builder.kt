package com.bumble.appyx.core.builder

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

// Changing this to an interface would be a breaking change
@Suppress("UnnecessaryAbstractClass")
abstract class Builder<P> {
    abstract fun build(buildContext: BuildContext, payload: P): Node
}
