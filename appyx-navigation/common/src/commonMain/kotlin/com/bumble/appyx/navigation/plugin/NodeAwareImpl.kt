package com.bumble.appyx.navigation.plugin

import com.bumble.appyx.navigation.node.AbstractNode


class NodeAwareImpl<N : AbstractNode> : NodeAware<N> {
    override lateinit var node: N
        private set

    override fun init(node: N) {
        this.node = node
    }
}
