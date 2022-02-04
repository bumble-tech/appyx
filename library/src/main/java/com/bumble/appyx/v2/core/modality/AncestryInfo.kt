package com.bumble.appyx.v2.core.modality

import com.bumble.appyx.v2.core.node.ParentNode

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: ParentNode<*>,
    ) : AncestryInfo()

}
