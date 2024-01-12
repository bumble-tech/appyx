package com.bumble.appyx.navigation.node.container

import com.bumble.appyx.navigation.MainNavItem
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode


class MainNavNode(
    nodeContext: NodeContext,
) : AppyxMaterial3NavNode<MainNavItem>(
    nodeContext = nodeContext,
    navTargets = MainNavItem.values().toList(),
    navTargetResolver = MainNavItem.resolver,
    initialActiveElement = MainNavItem.SPOTLIGHT
)
