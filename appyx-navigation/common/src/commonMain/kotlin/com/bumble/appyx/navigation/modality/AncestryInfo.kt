package com.bumble.appyx.navigation.modality

import com.bumble.appyx.navigation.node.Node

sealed class AncestryInfo {

    object Root : AncestryInfo()

    data class Child(
        val anchor: Node<*>,
    ) : AncestryInfo()

}
