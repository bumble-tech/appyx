package com.bumble.appyx.sandbox.client.mvicoreexample

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.NavTarget
import kotlinx.parcelize.Parcelize

class MviCoreExampleNode(
    view: ParentNodeView<NavTarget>,
    buildContext: BuildContext,
    plugins: List<Plugin>,
    backStack: BackStack<NavTarget>,
) : ParentNode<NavTarget>(
    view = view,
    navModel = backStack,
    buildContext = buildContext,
    plugins = plugins
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Child1 : NavTarget()

        @Parcelize
        object Child2 : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child1 -> MviCoreChildNode1(buildContext)
            is NavTarget.Child2 -> MviCoreChildNode2(buildContext)
        }
}
