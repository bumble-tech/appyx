package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class AbstractParentNodeView<NavTarget : Any> : ParentNodeView<NavTarget> {

    final override lateinit var node: ParentNode<NavTarget>
        private set

    override fun init(node: ParentNode<NavTarget>) {
        this.node = node
    }

    @Composable
    override fun View(modifier: Modifier) {
        node.NodeView(modifier = modifier)
    }
}
