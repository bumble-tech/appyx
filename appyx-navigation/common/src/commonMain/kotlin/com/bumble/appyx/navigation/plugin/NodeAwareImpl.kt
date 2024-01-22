package com.bumble.appyx.navigation.plugin

import com.bumble.appyx.navigation.node.Node


class NodeAwareImpl<N : Node<*>> : NodeAware<N> {
    override lateinit var node: N
        private set

    override fun init(node: N) {
        this.node = node
    }
}
