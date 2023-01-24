package com.bumble.appyx.navigation.modality

import com.bumble.appyx.navigation.node.ParentNode

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: ParentNode<*>,
    ) : AncestryInfo()

}
