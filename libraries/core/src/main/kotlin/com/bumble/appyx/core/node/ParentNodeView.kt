package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.plugin.NodeAware

interface ParentNodeView<NavTarget : Any> : NodeView, NodeAware<ParentNode<NavTarget>> {
    @Composable
    fun ParentNode<NavTarget>.NodeView(modifier: Modifier)
}
