package com.bumble.appyx.v2.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.plugin.NodeAware

abstract class AbstractNodeView<N : Node> : NodeView, NodeAware<N> {

    final override lateinit var node: N
        private set

    override fun init(node: N) {
        this.node = node
    }

    @Composable
    override fun View(modifier: Modifier) {
        node.NodeView(modifier = modifier)
    }

    @Composable
    abstract fun N.NodeView(modifier: Modifier)

}
