package com.bumble.appyx.v2.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<Routing : Any> : ParentNodeView<Routing>() {

    @Composable
    override fun ParentNode<Routing>.NodeView(modifier: Modifier) = Unit

}
