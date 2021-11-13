package com.github.zsoltk.composeribs.core.plugin

import com.github.zsoltk.composeribs.core.node.Node


class NodeAwareImpl : NodeAware {
    override lateinit var node: Node
        private set

    override fun init(node: Node) {
        this.node = node
    }
}
