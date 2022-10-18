package com.bumble.appyx.sandbox.client.navmodels

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sandbox.TextButton
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode
import com.bumble.appyx.sandbox.client.cardsexample.CardsExampleNode
import com.bumble.appyx.sandbox.client.combined.CombinedNavModelNode
import com.bumble.appyx.sandbox.client.modal.ModalExampleNode
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode
import com.bumble.appyx.sandbox.client.spotlightadvancedexample.SpotlightAdvancedExampleNode
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode
import kotlinx.parcelize.Parcelize

class NavModelExamplesNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        savedStateMap = buildContext.savedStateMap,
        initialElement = NavTarget.Picker
    )
) : ParentNode<NavModelExamplesNode.NavTarget>(
    navModel = backStack,
    buildContext = buildContext,
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Picker : NavTarget()

        @Parcelize
        object BackStackExample : NavTarget()

        @Parcelize
        object SpotlightExample : NavTarget()

        @Parcelize
        object SpotlightAdvancedExample : NavTarget()

        @Parcelize
        object CardsExample : NavTarget()

        @Parcelize
        object TilesExample : NavTarget()

        @Parcelize
        object ModalExample : NavTarget()

        @Parcelize
        object CombinedNavModelExample : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Picker -> node(buildContext) { modifier -> NavModelPicker(modifier) }
            is NavTarget.BackStackExample -> BackStackExampleNode(buildContext)
            is NavTarget.SpotlightExample -> SpotlightExampleNode(buildContext)
            is NavTarget.SpotlightAdvancedExample -> SpotlightAdvancedExampleNode(buildContext)
            is NavTarget.CardsExample -> CardsExampleNode(buildContext)
            is NavTarget.ModalExample -> ModalExampleNode(buildContext)
            is NavTarget.TilesExample -> TilesExampleNode(buildContext)
            is NavTarget.CombinedNavModelExample -> CombinedNavModelNode(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Surface(color = MaterialTheme.colors.background) {
            Children(
                modifier = modifier.fillMaxSize(),
                navModel = backStack,
                transitionHandler = rememberBackstackSlider()
            ) {
                children<NavTarget> { child, descriptor ->
                    val color = when (descriptor.element) {
                        is NavTarget.BackStackExample -> Color.LightGray
                        else -> MaterialTheme.colors.background
                    }
                    child(modifier = Modifier.background(color))
                }
            }
        }
    }

    @Composable
    private fun NavModelPicker(modifier: Modifier = Modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton("Backstack example") { backStack.push(NavTarget.BackStackExample) }
                TextButton("Spotlight Example") { backStack.push(NavTarget.SpotlightExample) }
                TextButton("SpotlightAdvanced example") { backStack.push(NavTarget.SpotlightAdvancedExample) }
                TextButton("Cards example") { backStack.push(NavTarget.CardsExample) }
                TextButton("Tiles example") { backStack.push(NavTarget.TilesExample) }
                TextButton("Modal example") { backStack.push(NavTarget.ModalExample) }
                TextButton("Combined navModel") { backStack.push(NavTarget.CombinedNavModelExample) }
            }
        }
    }
}
