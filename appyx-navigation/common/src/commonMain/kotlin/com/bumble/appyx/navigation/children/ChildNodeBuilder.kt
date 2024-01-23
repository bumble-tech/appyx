package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node

fun interface ChildNodeBuilder<NavTarget> {

    fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*>
}
