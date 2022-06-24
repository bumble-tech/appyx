package com.bumble.appyx.core.modality

import com.bumble.appyx.core.node.ParentNode

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: ParentNode<*>,
    ) : AncestryInfo()

}
