package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ParentNodeView<NavTarget : Any> : NodeView {

    @Composable
    fun ParentNode<NavTarget>.NodeView(modifier: Modifier)

    /**
     * Do not override this function. Parent views should implement NodeView method.
     */
    @Suppress("UNCHECKED_CAST")
    @Composable
    override fun View(modifier: Modifier) {
        val node = LocalNode.current as? ParentNode<NavTarget>
            ?: error("${this::class.qualifiedName} is not provided to the appropriate ParentNode")
        node.NodeView(modifier = modifier)
    }
}
