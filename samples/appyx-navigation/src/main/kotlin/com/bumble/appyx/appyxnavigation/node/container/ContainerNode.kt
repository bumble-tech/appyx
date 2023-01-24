package com.bumble.appyx.appyxnavigation.node.container

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val backStackModel: BackStackModel<NavTarget> = BackStackModel(
        initialElement = NavTarget.DatingCards,
        savedStateMap = buildContext.savedStateMap
    )
) : ParentNode<ContainerNode.NavTarget>(
    buildContext = buildContext,
    transitionModel = backStackModel
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object DatingCards : NavTarget()

        @Parcelize
        object SpotlightExperiment : NavTarget()

        @Parcelize
        object SpotlightExperimentDebug : NavTarget()

        @Parcelize
        object BackStackExperimentDebug : NavTarget()

        @Parcelize
        object PromoterExperiment : NavTarget()
    }


    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.DatingCards -> DatingCardsNode(buildContext)
            is NavTarget.SpotlightExperiment -> TODO()
            else -> TODO()
        }

    @Composable
    override fun View(modifier: Modifier) {
        // TODO
    }
}
