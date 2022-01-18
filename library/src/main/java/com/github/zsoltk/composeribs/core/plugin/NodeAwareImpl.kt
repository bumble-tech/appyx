package com.github.zsoltk.composeribs.core.plugin

import com.github.zsoltk.composeribs.core.node.Node


class NodeAwareImpl<N : Node> : NodeAware<N> {
    override lateinit var node: N
        private set

    override fun init(node: N) {
        this.node = node
    }
}
