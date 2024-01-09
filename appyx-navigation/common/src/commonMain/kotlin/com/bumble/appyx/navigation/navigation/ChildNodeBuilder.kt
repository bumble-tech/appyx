package com.bumble.appyx.navigation.navigation

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

fun interface ChildNodeBuilder<NavTarget> {

    fun buildChildNode(navTarget: NavTarget, buildContext: BuildContext): Node
}
