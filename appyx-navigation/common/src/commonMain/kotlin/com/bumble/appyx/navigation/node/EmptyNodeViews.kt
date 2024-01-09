package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<ChildReference : Any> : ParentNodeView<ChildReference> {

    @Composable
    override fun ParentNode<ChildReference>.NodeView(modifier: Modifier) = Unit

}
