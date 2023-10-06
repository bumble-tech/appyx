package com.bumble.appyx.navigation.node.main

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode

class MainNode(
    buildContext: BuildContext,
) : AppyxMaterial3NavNode<MainNavItem>(
    buildContext = buildContext,
    navTargets = MainNavItem.values().toList(),
    navTargetResolver = MainNavItem.resolver,
    initialActiveElement = MainNavItem.HOME
)
