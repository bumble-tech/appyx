package com.github.zsoltk.composeribs.core

open class LeafNode : Node<Nothing>() {

    override fun resolve(routing: Nothing): Node<*> {
        error("Framework error, this should never happen")
    }
}
