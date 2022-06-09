package com.bumble.appyx.v2.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.plugin.NodeAware

abstract class ParentNodeView<Routing : Any> : NodeView, NodeAware<ParentNode<Routing>> {

    final override lateinit var node: ParentNode<Routing>
        private set

    override fun init(node: ParentNode<Routing>) {
        this.node = node
    }

    @Composable
    override fun View(modifier: Modifier) {
        node.NodeView(modifier = modifier)
    }

    @Composable
    abstract fun ParentNode<Routing>.NodeView(modifier: Modifier)

}
