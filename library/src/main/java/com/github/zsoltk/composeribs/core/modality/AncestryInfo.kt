package com.github.zsoltk.composeribs.core.modality

import com.github.zsoltk.composeribs.core.node.Node

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: Node,
    ) : AncestryInfo()

}
