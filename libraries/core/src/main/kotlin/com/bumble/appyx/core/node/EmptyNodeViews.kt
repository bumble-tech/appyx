package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<NavTarget : Any> : ParentNodeView<NavTarget> {

    @Composable
    override fun ParentNode<NavTarget>.NodeView(modifier: Modifier) = Unit

}
