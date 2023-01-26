package com.bumble.appyx.navigation.navigation

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

interface Resolver<NavTarget> {
    fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node
}
