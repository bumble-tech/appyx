package com.bumble.appyx.sandbox.client.container

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.collections.immutableListOf
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.rememberCombinedHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sandbox.TextButton
import com.bumble.appyx.sandbox.client.blocker.BlockerExampleNode
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.BlockerExample
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.Customisations
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.IntegrationPointExample
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.LazyExamples
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.MviCoreExample
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.MviCoreLeafExample
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.NavModelExamples
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.Picker
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.SharedElementExample
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.SharedElementPager
import com.bumble.appyx.sandbox.client.container.ContainerNode.NavTarget.SharedElementWithMovableContentExample
import com.bumble.appyx.sandbox.client.customisations.CustomisationsNode
import com.bumble.appyx.sandbox.client.explicitnavigation.ExplicitNavigationExampleActivity
import com.bumble.appyx.sandbox.client.integrationpoint.IntegrationPointExampleNode
import com.bumble.appyx.sandbox.client.interop.InteropExampleActivity
import com.bumble.appyx.sandbox.client.list.LazyListContainerNode
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleBuilder
import com.bumble.appyx.sandbox.client.mvicoreexample.leaf.MviCoreLeafBuilder
import com.bumble.appyx.sandbox.client.navmodels.NavModelExamplesNode
import com.bumble.appyx.sandbox.client.sharedelement.SharedElementExampleNode
import com.bumble.appyx.sandbox.client.sharedelement.with_pager.SharedElementPagerParentNode
import com.bumble.appyx.utils.customisations.NodeCustomisation
import kotlinx.parcelize.Parcelize

class ContainerNode internal constructor(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = Picker,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext,
) {

    class Customisation(val name: String? = null) : NodeCustomisation

    private val label: String? = buildContext.getOrDefault(Customisation()).name

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Picker : NavTarget()

        @Parcelize
        object LazyExamples : NavTarget()

        @Parcelize
        object SharedElementExample : NavTarget()

        @Parcelize
        object SharedElementWithMovableContentExample : NavTarget()

        @Parcelize
        object IntegrationPointExample : NavTarget()

        @Parcelize
        object NavModelExamples : NavTarget()

        @Parcelize
        object BlockerExample : NavTarget()

        @Parcelize
        object Customisations : NavTarget()

        @Parcelize
        object MviCoreExample : NavTarget()

        @Parcelize
        object MviCoreLeafExample : NavTarget()

        @Parcelize
        object SharedElementPager : NavTarget()
    }

    @Suppress("ComplexMethod")
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is Picker -> node(buildContext) { modifier -> ExamplesList(modifier) }
            is NavModelExamples -> NavModelExamplesNode(buildContext)
            is SharedElementExample -> SharedElementExampleNode(buildContext)
            is SharedElementWithMovableContentExample -> SharedElementExampleNode(
                buildContext,
                hasMovableContent = true
            )

            is LazyExamples -> LazyListContainerNode(buildContext)
            is IntegrationPointExample -> IntegrationPointExampleNode(buildContext)
            is BlockerExample -> BlockerExampleNode(buildContext)
            is Customisations -> CustomisationsNode(buildContext)
            is MviCoreExample -> MviCoreExampleBuilder().build(
                buildContext,
                "MVICore initial state"
            )

            is MviCoreLeafExample -> MviCoreLeafBuilder().build(
                buildContext,
                "MVICore leaf initial state"
            )

            is SharedElementPager -> SharedElementPagerParentNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            navModel = backStack,
            transitionHandler = rememberCombinedHandler(
                handlers = immutableListOf(rememberBackstackSlider(), rememberBackstackFader())
            )
        )
    }

    @Composable
    fun ExamplesList(modifier: Modifier = Modifier) {
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
                label?.let {
                    Text(it, textAlign = TextAlign.Center)
                }
                TextButton("Shared element Pager") { backStack.push(SharedElementPager) }
                TextButton("Shared element ") { backStack.push(SharedElementExample) }
                TextButton("Shared element with movable content") {
                    backStack.push(
                        SharedElementWithMovableContentExample
                    )
                }
                TextButton("NavModel Examples") { backStack.push(NavModelExamples) }
                TextButton("Customisations Example") { backStack.push(Customisations) }
                TextButton("Explicit navigation example") {
                    integrationPoint.activityStarter.startActivity {
                        Intent(this, ExplicitNavigationExampleActivity::class.java)
                    }
                }
                TextButton("Integration point example") {
                    backStack.push(IntegrationPointExample)
                }
                TextButton("RIBs interop example") {
                    integrationPoint.activityStarter.startActivity {
                        Intent(this, InteropExampleActivity::class.java)
                    }
                }
                TextButton("Lazy Examples") { backStack.push(LazyExamples) }
                TextButton("Blocker") { backStack.push(BlockerExample) }
                TextButton("MVICore Example") { backStack.push(MviCoreExample) }
                TextButton("MVICore Leaf Example") { backStack.push(MviCoreLeafExample) }
            }
        }
    }

}
