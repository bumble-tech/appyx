package com.bumble.appyx.interop.v1v2

import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.interop.v1v2.V1V2View.Dependencies
import com.bumble.appyx.interop.v1v2.V1V2View.Factory
import com.bumble.appyx.v2.core.integration.NodeFactory
import com.bumble.appyx.v2.core.node.Node

class V1V2Node<N : Node>(
    buildParams: BuildParams<*>,
    nodeFactory: NodeFactory<N>
) : com.badoo.ribs.core.Node<V1V2View<N>>(
    buildParams = buildParams,
    viewFactory = Factory<N>().invoke(object : Dependencies<N> {
        override val nodeFactory: NodeFactory<N> = nodeFactory
    })
)
