package com.bumble.appyx.navigation.navigation

import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

interface Resolver<InteractionTarget> {
    fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node
}
