package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Stable
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

@Stable
fun interface NodeFactory<N : Node> {
    fun create(buildContext: BuildContext): N
}
