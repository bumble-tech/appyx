package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ParentNodeView<InteractionTarget : Any> : NodeView {

    @Composable
    fun ParentNode<InteractionTarget>.NodeView(modifier: Modifier)

    /**
     * Do not override this function. Parent views should implement NodeView method.
     */
    @Suppress("UNCHECKED_CAST")
    @Composable
    override fun View(modifier: Modifier) {
        val node = LocalNode.current as? ParentNode<InteractionTarget>
            ?: error("${this::class} is not provided to the appropriate ParentNode")
        node.NodeView(modifier = modifier)
    }
}
