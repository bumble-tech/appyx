package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Stable
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode

@Stable
fun interface NodeFactory<N : AbstractNode> {
    fun create(nodeContext: NodeContext): N
}
