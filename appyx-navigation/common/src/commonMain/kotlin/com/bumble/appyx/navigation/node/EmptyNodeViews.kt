package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class EmptyNodeView<NavTarget : Any> : NodeView<NavTarget> {

    @Composable
    override fun Node<NavTarget>.NodeContent(modifier: Modifier) = Unit

}
