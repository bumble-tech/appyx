package com.bumble.appyx.appyxnavigation.node.spotlight.debug

import android.os.Parcelable
import com.bumble.appyx.appyxnavigation.node.spotlight.SpotlightNode
import com.bumble.appyx.appyxnavigation.node.spotlight.debug.SpotlightDebugNode.NavTarget
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlinx.parcelize.Parcelize

class SpotlightDebugNode(
    buildContext: BuildContext,
    spotlightModel: SpotlightModel<NavTarget> = SpotlightModel(
        items = List(7) { NavTarget.Child(it) },
        initialActiveIndex = 0f,
        initialActiveWindow = 1f
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    transitionModel = spotlightModel
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        node(buildContext) {}
}
