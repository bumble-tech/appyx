package com.github.zsoltk.composeribs.core

abstract class LeafNode(
    savedStateMap: SavedStateMap?
) : Node<Nothing>(
    savedStateMap = null,
) {

    override fun resolve(routing: Nothing, savedStateMap: SavedStateMap?): Node<*> {
        error("Framework error, this should never happen")
    }
}
