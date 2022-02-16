package com.bumble.appyx.interop.v1v2

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.v2.core.integration.NodeFactory
import com.bumble.appyx.v2.core.node.Node

class V1V2Builder<N : Node>(private val nodeFactory: NodeFactory<N>) :
    SimpleBuilder<V1V2Node<N>>() {
    override fun build(buildParams: BuildParams<Nothing?>): V1V2Node<N> {
        return V1V2Node(buildParams, nodeFactory = nodeFactory)
    }
}
