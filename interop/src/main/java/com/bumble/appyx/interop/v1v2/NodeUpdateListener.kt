package com.bumble.appyx.interop.v1v2

import com.bumble.appyx.v2.core.node.Node

interface NodeUpdateListener<N : Node> {
    fun onNodeUpdated(node: N)
}
