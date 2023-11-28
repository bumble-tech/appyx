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
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode
import com.bumble.appyx.navigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.navigation.node.container.ContainerNode.InteractionTarget
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
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Selector),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )

) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object Selector : InteractionTarget()

        @Parcelize
        object PermanentChild : InteractionTarget()

//        @Parcelize
//        object DatingCards : InteractionTarget()

        @Parcelize
        object SpotlightExperiment : InteractionTarget()

        @Parcelize
        object ObservingTransitionsExample : InteractionTarget()

        @Parcelize
        object BackStackExperimentDebug : InteractionTarget()

        @Parcelize
        object BackStack : InteractionTarget()

        @Parcelize
        object Modal : InteractionTarget()

        @Parcelize
        object PromoterExperiment : InteractionTarget()
    }


    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Selector -> node(buildContext) { modifier ->
                Selector(modifier)
            }

            is InteractionTarget.PermanentChild -> PermanentChildNode(buildContext)
//            is InteractionTarget.DatingCards -> DatingCardsNode(buildContext)
            is InteractionTarget.SpotlightExperiment -> SpotlightNode(buildContext)
            is InteractionTarget.ObservingTransitionsExample -> SpotlightObserveTransitionsExampleNode(
                buildContext
            )

            is InteractionTarget.BackStack -> BackStackExamplesNode(buildContext)
            is InteractionTarget.BackStackExperimentDebug -> BackstackDebugNode(buildContext)
            is InteractionTarget.Modal -> ModalExamplesNode(buildContext)
            is InteractionTarget.PromoterExperiment -> PromoterNode(buildContext)
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
////                    backStack.push(InteractionTarget.DatingCards)
//                }
                TextButton(text = "Spotlight") {
                    backStack.push(InteractionTarget.SpotlightExperiment)
                }
                TextButton(text = "Observe transitions example") {
                    backStack.push(InteractionTarget.ObservingTransitionsExample)
                }
                TextButton(text = "Backstack Examples") {
                    backStack.push(InteractionTarget.BackStack)
                }
                TextButton(text = "Backstack Debug") {
                    backStack.push(InteractionTarget.BackStackExperimentDebug)
                }
                TextButton(text = "Promoter") {
                    backStack.push(InteractionTarget.PromoterExperiment)
                }
                TextButton(text = "Permanent Child") {
                    backStack.push(InteractionTarget.PermanentChild)
                }
                TextButton(text = "Modal") {
                    backStack.push(InteractionTarget.Modal)
                }
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = modifier
                .fillMaxSize()
        )
    }
}
