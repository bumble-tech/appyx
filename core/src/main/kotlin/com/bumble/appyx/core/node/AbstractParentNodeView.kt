package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class AbstractParentNodeView<Routing : Any> : ParentNodeView<Routing> {

    final override lateinit var node: ParentNode<Routing>
        private set

    override fun init(node: ParentNode<Routing>) {
        this.node = node
    }

    @Composable
    override fun View(modifier: Modifier) {
        node.NodeView(modifier = modifier)
    }
}
