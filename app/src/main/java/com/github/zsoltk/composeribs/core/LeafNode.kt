package com.github.zsoltk.composeribs.core

abstract class LeafNode : Node<Nothing>() {

    override fun resolve(routing: Nothing): Node<*> {
        error("Framework error, this should never happen")
    }
}
