package com.bumble.appyx.appyxnavigation.node.selector

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import kotlinx.parcelize.Parcelize

class SelectorNode(
    buildContext: BuildContext,
    private val outputFunc: (Output) -> Unit
) : ParentNode<SelectorNode.NavTarget>(
    interactionModel = EmptyInteractionModel(),
    buildContext = buildContext
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

    sealed class Output {
        object OpenDatingCards : Output()
        object OpenSpotlightExperiment : Output()
        object OpenSpotlightExperimentDebug : Output()
        object OpenBackStackExperimentDebug : Output()
        object OpenPromoterExperiment : Output()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.DatingCards -> node(buildContext) {
                // TODO: add node in list when permanent children are available
            }
            is NavTarget.SpotlightExperiment -> node(buildContext) {
                // TODO: add node in list when permanent children are available
            }
            is NavTarget.SpotlightExperimentDebug -> node(buildContext) {
                // TODO: add node in list when permanent children are available
            }
            is NavTarget.BackStackExperimentDebug -> node(buildContext) {
                // TODO: add node in list when permanent children are available
            }
            is NavTarget.PromoterExperiment -> node(buildContext) {
                // TODO: add node in list when permanent children are available
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)

        ) {
            item {
                ListItem(title = "Dating Cards") {
                    outputFunc(Output.OpenDatingCards)
                }
            }
            item {
                ListItem(title = "Spotlight") {
                    outputFunc(Output.OpenSpotlightExperiment)
                }
            }
            item {
                ListItem(title = "Spotlight Debug") {
                    outputFunc(Output.OpenSpotlightExperimentDebug)
                }
            }
            item {
                ListItem(title = "Backstack") {
                    outputFunc(Output.OpenBackStackExperimentDebug)
                }
            }
            item {
                ListItem(title = "Promoter") {
                    outputFunc(Output.OpenPromoterExperiment)
                }
            }
        }
    }

    @Composable
    private fun ListItem(
        title: String,
        action: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(height = 100.dp)
                .fillMaxWidth()
                .clickable {
                    action()
                }
                .background(Color.White)
        ) {
            Text(text = title)
        }
    }


    private class EmptyInteractionModel<NavTarget : Any> : InteractionModel<NavTarget, Unit>(
        model = EmptyTransitionModel(),
        interpolator = {
            object : Interpolator<NavTarget, Unit> {
                override fun mapFrame(segment: TransitionModel.Segment<Unit>): List<FrameModel<NavTarget>> =
                    emptyList()
            }
        }
    )

    private class EmptyTransitionModel<NavTarget> : BaseTransitionModel<NavTarget, Unit>() {
        override val initialState: Unit = Unit

        override fun Unit.destroyedElements(): Set<NavElement<NavTarget>> = emptySet()

        override fun Unit.availableElements(): Set<NavElement<NavTarget>> = emptySet()
    }
}


