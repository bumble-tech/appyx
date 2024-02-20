package com.bumble.appyx.utils.ribshelpers.builder

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node

// Changing this to an interface would be a breaking change
@Suppress("UnnecessaryAbstractClass")
abstract class SimpleBuilder {
    abstract fun build(nodeContext: NodeContext): Node<*>
}
