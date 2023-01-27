package com.bumble.appyx.appyxnavigation.node.container

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.DatingCards,
            savedStateMap = buildContext.savedStateMap
        ),
        interpolator = { BackStackSlider(it) }
    )

) : ParentNode<ContainerNode.NavTarget>(
    buildContext = buildContext,
    interactionModel = backStack
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
