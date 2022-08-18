package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.plugin.NodeAware

interface ParentNodeView<Routing : Any> : NodeView, NodeAware<ParentNode<Routing>> {
    @Composable
    fun ParentNode<Routing>.NodeView(modifier: Modifier)
}
