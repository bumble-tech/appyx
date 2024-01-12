package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ParentNodeView<NavTarget : Any> : NodeView {

    @Composable
    fun Node<NavTarget>.NodeView(modifier: Modifier)

    /**
     * Do not override this function. Parent views should implement NodeView method.
     */
    @Suppress("UNCHECKED_CAST")
    @Composable
    override fun Content(modifier: Modifier) {
        val node = LocalNode.current as? Node<NavTarget>
            ?: error("${this::class} is not provided to the appropriate ParentNode")
        node.NodeView(modifier = modifier)
    }
}
