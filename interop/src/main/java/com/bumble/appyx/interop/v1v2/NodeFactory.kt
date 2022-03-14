package com.bumble.appyx.interop.v1v2

import com.bumble.appyx.v2.core.node.Node

typealias BuildContextV1 = com.badoo.ribs.core.modality.BuildContext
typealias BuildContextV2 = com.bumble.appyx.v2.core.modality.BuildContext

fun interface NodeFactory<N : Node> {

    fun create(buildContextV1: BuildContextV1, buildContextV2: BuildContextV2): N
}
