package com.bumble.appyx.navigation.builder

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

// Changing this to an interface would be a breaking change
@Suppress("UnnecessaryAbstractClass")
abstract class SimpleBuilder {
    abstract fun build(buildContext: BuildContext): Node
}
