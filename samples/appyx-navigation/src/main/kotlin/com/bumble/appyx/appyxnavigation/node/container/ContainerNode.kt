package com.bumble.appyx.appyxnavigation.node.container

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.appyxnavigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.appyxnavigation.node.container.ContainerNode.NavTarget
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.appyxnavigation.node.promoter.PromoterNode
import com.bumble.appyx.appyxnavigation.node.selector.SelectorNode
import com.bumble.appyx.appyxnavigation.node.spotlight.SpotlightNode
import com.bumble.appyx.appyxnavigation.node.spotlight.debug.SpotlightDebugNode
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackCrossfader
import com.bumble.appyx.transitionmodel.backstack.operation.push
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val coroutineScope: CoroutineScope,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Samples,
            savedStateMap = buildContext.savedStateMap
        ),
        interpolator = { BackStackCrossfader() }
    )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
    interactionModel = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Samples : NavTarget()

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
            is NavTarget.Samples -> SelectorNode(buildContext) {
                backStack.push(
                    when (it) {
                        is SelectorNode.Output.OpenDatingCards -> NavTarget.DatingCards
                        is SelectorNode.Output.OpenSpotlightExperiment -> NavTarget.SpotlightExperiment
                        is SelectorNode.Output.OpenSpotlightExperimentDebug -> NavTarget.SpotlightExperimentDebug
                        is SelectorNode.Output.OpenBackStackExperimentDebug -> NavTarget.BackStackExperimentDebug
                        is SelectorNode.Output.OpenPromoterExperiment -> NavTarget.PromoterExperiment
                    }
                )
            }
            is NavTarget.DatingCards -> DatingCardsNode(buildContext)
            is NavTarget.SpotlightExperiment -> SpotlightNode(buildContext, coroutineScope)
            is NavTarget.SpotlightExperimentDebug -> SpotlightDebugNode(
                buildContext,
                coroutineScope
            )
            is NavTarget.BackStackExperimentDebug -> BackstackDebugNode(buildContext)
            is NavTarget.PromoterExperiment -> PromoterNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            interactionModel = backStack,
            modifier = modifier
                .fillMaxSize()
        )
    }
}
