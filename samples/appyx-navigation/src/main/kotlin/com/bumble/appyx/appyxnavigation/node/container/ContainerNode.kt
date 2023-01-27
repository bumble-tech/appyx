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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.appyxnavigation.node.backstack.debug.BackstackDebugNode
import com.bumble.appyx.appyxnavigation.node.container.ContainerNode.NavTarget
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.appyxnavigation.node.promoter.PromoterNode
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
            initialTarget = NavTarget.DatingCards,
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
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Selector(
                modifier = Modifier.weight(weight = 0.1f)
            )
            Children(
                modifier = Modifier.weight(weight = 0.9f),
                interactionModel = backStack,
            )
        }
    }

    @Composable
    private fun Selector(
        modifier: Modifier = Modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button({ backStack.push(NavTarget.DatingCards) }) { Text("1") }
                Button({ backStack.push(NavTarget.SpotlightExperiment) }) { Text("2") }
                Button({ backStack.push(NavTarget.SpotlightExperimentDebug) }) { Text("3") }
                Button({ backStack.push(NavTarget.BackStackExperimentDebug) }) { Text("4") }
                Button({ backStack.push(NavTarget.PromoterExperiment) }) { Text("5") }
            }
        }
    }
}
