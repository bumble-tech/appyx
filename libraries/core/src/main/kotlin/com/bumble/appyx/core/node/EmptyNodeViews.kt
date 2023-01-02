package com.bumble.appyx.core.node

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object EmptyNodeView : NodeView {

    @Composable
    override fun View(modifier: Modifier) = Unit
}

class EmptyParentNodeView<NavTarget : Parcelable> : ParentNodeView<NavTarget> {

    @Composable
    override fun ParentNode<NavTarget>.NodeView(modifier: Modifier) = Unit

}
