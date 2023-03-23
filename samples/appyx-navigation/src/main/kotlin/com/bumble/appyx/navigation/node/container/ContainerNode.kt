package com.bumble.appyx.navigation.node.container

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode
import com.bumble.appyx.navigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.navigation.node.container.ContainerNode.NavTarget
import com.bumble.appyx.navigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.navigation.node.promoter.PromoterNode
import com.bumble.appyx.navigation.node.spotlight.SpotlightNode
import com.bumble.appyx.navigation.node.spotlight.debug.SpotlightDebugNode
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.operation.push
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Selector),
            savedStateMap = buildContext.savedStateMap,
            key = ContainerNode::class.java.name
        ),
        motionController = { BackStackSlider(it) }
    )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
    interactionModel = backStack,
    key = ContainerNode::class.java.name
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Selector : NavTarget()

        @Parcelize
        object DatingCards : NavTarget()

        @Parcelize
        object SpotlightExperiment : NavTarget()

        @Parcelize
        object SpotlightExperimentDebug : NavTarget()

        @Parcelize
        object BackStackExperimentDebug : NavTarget()

        @Parcelize
        object BackStack : NavTarget()

        @Parcelize
        object PromoterExperiment : NavTarget()
    }


    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Selector -> node(buildContext) { modifier ->
                Selector(modifier)
            }
            is NavTarget.DatingCards -> DatingCardsNode(buildContext)
            is NavTarget.SpotlightExperiment -> SpotlightNode(buildContext)
            is NavTarget.SpotlightExperimentDebug -> SpotlightDebugNode(buildContext)
            is NavTarget.BackStack -> BackStackExamplesNode(buildContext)
            is NavTarget.BackStackExperimentDebug -> BackstackDebugNode(buildContext)
            is NavTarget.PromoterExperiment -> PromoterNode(buildContext)
        }


    @Composable
    private fun Selector(modifier: Modifier = Modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(text = "Dating Cards") {
                    backStack.push(NavTarget.DatingCards)
                }
                TextButton(text = "Spotlight") {
                    backStack.push(NavTarget.SpotlightExperiment)
                }
                TextButton(text = "Spotlight Debug") {
                    backStack.push(NavTarget.SpotlightExperimentDebug)
                }
                TextButton(text = "Backstack Examples") {
                    backStack.push(NavTarget.BackStack)
                }
                TextButton(text = "Backstack Debug") {
                    backStack.push(NavTarget.BackStackExperimentDebug)
                }
                TextButton(text = "Promoter") {
                    backStack.push(NavTarget.PromoterExperiment)
                }
            }
        }
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
