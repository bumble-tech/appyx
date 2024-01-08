package com.bumble.appyx.navigation.navigation

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

interface ChildNodeBuilder<T> {

    fun buildChildNode(reference: T, buildContext: BuildContext): Node
}
