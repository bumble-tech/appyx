package com.bumble.appyx.appyxnavigation.node.spotlight

import android.os.Parcelable
import com.bumble.appyx.appyxnavigation.node.spotlight.SpotlightNode.NavTarget
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlinx.parcelize.Parcelize

class SpotlightNode(
    buildContext: BuildContext,
    spotlightModel: SpotlightModel<NavTarget> = SpotlightModel(
        items = listOf(),
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
        when (navTarget) {
            is NavTarget.Child -> node(buildContext) {}
        }

}
