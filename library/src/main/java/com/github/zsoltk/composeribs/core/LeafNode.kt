package com.github.zsoltk.composeribs.core

import com.github.zsoltk.composeribs.core.modality.BuildContext

abstract class LeafNode(
    buildContext: BuildContext
) : Node<Nothing>(
    buildContext = buildContext,
) {

    override fun resolve(routing: Nothing, buildContext: BuildContext): Node<*> {
        error("Framework error, this should never happen")
    }
}
