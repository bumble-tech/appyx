package com.bumble.appyx.appyxnavigation.node.container

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.appyxnavigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.appyxnavigation.node.promoter.PromoterNode
import com.bumble.appyx.appyxnavigation.node.spotlight.SpotlightNode
import com.bumble.appyx.appyxnavigation.node.spotlight.debug.SpotlightDebugNode
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackCrossfader
import com.bumble.appyx.transitionmodel.backstack.operation.push
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
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
            is NavTarget.SpotlightExperiment -> SpotlightNode(buildContext)
            is NavTarget.SpotlightExperimentDebug -> SpotlightDebugNode(buildContext)
            is NavTarget.BackStackExperimentDebug -> BackstackDebugNode(buildContext)
            is NavTarget.PromoterExperiment -> PromoterNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Selector(
                backStack = backStack,
                modifier = Modifier.weight(weight = 0.1f)
            )
            Children(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(weight = 0.9f),
                interactionModel = backStack,
            )
        }
    }

    @Composable
    private fun Selector(
        modifier: Modifier = Modifier
    ) {
        var content by remember { mutableStateOf(1) }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button({ content = 0 }) { Text("1") }
                Button({ content = 1 }) { Text("2") }
                Button({ content = 2 }) { Text("3") }
                Button({ content = 3 }) { Text("4") }
                Button({ content = 4 }) { Text("5") }
            }
            when (content) {
                0 -> backStack.push(NavTarget.DatingCards)
                1 -> backStack.push(NavTarget.SpotlightExperiment)
                2 -> backStack.push(NavTarget.SpotlightExperimentDebug)
                3 -> backStack.push(NavTarget.BackStackExperimentDebug)
                4 -> backStack.push(NavTarget.PromoterExperiment)
                else -> backStack.push(NavTarget.SpotlightExperiment)
            }
        }
    }
}
