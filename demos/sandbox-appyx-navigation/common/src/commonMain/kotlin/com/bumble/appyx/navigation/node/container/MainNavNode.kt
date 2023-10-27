package com.bumble.appyx.navigation.node.container

import com.bumble.appyx.navigation.MainNavItem
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode


class MainNavNode(
    buildContext: BuildContext,
) : AppyxMaterial3NavNode<MainNavItem>(
    buildContext = buildContext,
    navTargets = MainNavItem.values().toList(),
    navTargetResolver = MainNavItem.resolver,
    initialActiveElement = MainNavItem.SPOTLIGHT
)
