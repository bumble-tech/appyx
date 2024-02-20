package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class NodeStub(
    nodeContext: NodeContext = NodeContext.root(savedStateMap = null),
) : LeafNode(
    nodeContext = nodeContext,
)
