package com.bumble.appyx.v2.core.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.plugin.NodeAware

abstract class AbstractNodeVew<N : Node> : NodeView, NodeAware<N> {

    private var initialised: Boolean by mutableStateOf(false)

    final override lateinit var node: N
        private set

    override fun init(node: N) {
        this.node = node
        initialised = true
    }

    @Composable
    override fun View(modifier: Modifier) {
        if (initialised) {
            node.NodeView(modifier = modifier)
        }
    }

    @Composable
    abstract fun N.NodeView(modifier: Modifier)

}
