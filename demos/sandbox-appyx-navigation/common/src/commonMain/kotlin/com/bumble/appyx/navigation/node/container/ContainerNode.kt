package com.bumble.appyx.navigation.node.container

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
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode
import com.bumble.appyx.navigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.navigation.node.container.ContainerNode.NavTarget
import com.bumble.appyx.navigation.node.modal.ModalExamplesNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.permanentchild.PermanentChildNode
import com.bumble.appyx.navigation.node.promoter.PromoterNode
import com.bumble.appyx.navigation.node.spotlight.SpotlightNode
import com.bumble.appyx.navigation.node.spotlight.SpotlightObserveTransitionsExampleNode
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class ContainerNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Selector),
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )

) : ParentNode<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Selector : NavTarget()

        @Parcelize
        object PermanentChild : NavTarget()

//        @Parcelize
//        object DatingCards : NavTarget()

        @Parcelize
        object SpotlightExperiment : NavTarget()

        @Parcelize
        object ObservingTransitionsExample : NavTarget()

        @Parcelize
        object BackStackExperimentDebug : NavTarget()

        @Parcelize
        object BackStack : NavTarget()

        @Parcelize
        object Modal : NavTarget()

        @Parcelize
        object PromoterExperiment : NavTarget()
    }


    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node =
        when (navTarget) {
            is NavTarget.Selector -> node(nodeContext) { modifier ->
                Selector(modifier)
            }

            is NavTarget.PermanentChild -> PermanentChildNode(nodeContext)
//            is NavTarget.DatingCards -> DatingCardsNode(nodeContext)
            is NavTarget.SpotlightExperiment -> SpotlightNode(nodeContext)
            is NavTarget.ObservingTransitionsExample -> SpotlightObserveTransitionsExampleNode(
                nodeContext
            )

            is NavTarget.BackStack -> BackStackExamplesNode(nodeContext)
            is NavTarget.BackStackExperimentDebug -> BackstackDebugNode(nodeContext)
            is NavTarget.Modal -> ModalExamplesNode(nodeContext)
            is NavTarget.PromoterExperiment -> PromoterNode(nodeContext)
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
//                TextButton(text = "Dating Cards") {
////                    backStack.push(NavTarget.DatingCards)
//                }
                TextButton(text = "Spotlight") {
                    backStack.push(NavTarget.SpotlightExperiment)
                }
                TextButton(text = "Observe transitions example") {
                    backStack.push(NavTarget.ObservingTransitionsExample)
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
                TextButton(text = "Permanent Child") {
                    backStack.push(NavTarget.PermanentChild)
                }
                TextButton(text = "Modal") {
                    backStack.push(NavTarget.Modal)
                }
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = modifier
                .fillMaxSize()
        )
    }
}
