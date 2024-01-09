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
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
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
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Selector),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
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


    override fun buildChildNode(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Selector -> node(buildContext) { modifier ->
                Selector(modifier)
            }

            is NavTarget.PermanentChild -> PermanentChildNode(buildContext)
//            is NavTarget.DatingCards -> DatingCardsNode(buildContext)
            is NavTarget.SpotlightExperiment -> SpotlightNode(buildContext)
            is NavTarget.ObservingTransitionsExample -> SpotlightObserveTransitionsExampleNode(
                buildContext
            )

            is NavTarget.BackStack -> BackStackExamplesNode(buildContext)
            is NavTarget.BackStackExperimentDebug -> BackstackDebugNode(buildContext)
            is NavTarget.Modal -> ModalExamplesNode(buildContext)
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
        AppyxNavigationComponent(
            appyxComponent = backStack,
            modifier = modifier
                .fillMaxSize()
        )
    }
}
