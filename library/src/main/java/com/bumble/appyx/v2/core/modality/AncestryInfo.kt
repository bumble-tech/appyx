package com.bumble.appyx.v2.core.modality

import com.bumble.appyx.v2.core.node.Node

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: Node,
    ) : AncestryInfo()

}
