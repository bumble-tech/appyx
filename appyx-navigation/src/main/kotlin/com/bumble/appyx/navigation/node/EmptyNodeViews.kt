package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<InteractionTarget : Any> : ParentNodeView<InteractionTarget> {

    @Composable
    override fun ParentNode<InteractionTarget>.NodeView(modifier: Modifier) = Unit

}
