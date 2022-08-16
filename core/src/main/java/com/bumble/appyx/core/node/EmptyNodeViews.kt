package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<Routing : Any> : AbstractParentNodeView<Routing>() {

    @Composable
    override fun ParentNode<Routing>.NodeView(modifier: Modifier) = Unit

}
