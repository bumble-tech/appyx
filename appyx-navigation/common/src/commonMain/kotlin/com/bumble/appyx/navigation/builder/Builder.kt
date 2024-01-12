package com.bumble.appyx.navigation.builder

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode

// Changing this to an interface would be a breaking change
@Suppress("UnnecessaryAbstractClass")
abstract class Builder<P> {
    abstract fun build(nodeContext: NodeContext, payload: P): AbstractNode
}
