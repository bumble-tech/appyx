package com.bumble.appyx.sample.hilt.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.dagger.hilt.HiltAggregateNodeFactory
import com.bumble.appyx.dagger.hilt.getAggregateNodeFactory
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.sample.hilt.app.DaggerAppNode.NavTarget.ExternalDependencies
import com.bumble.appyx.sample.hilt.app.DaggerAppNode.NavTarget.OnlyInternalDependencies
import com.bumble.appyx.sample.hilt.library.externaldeps.ExternalDepsLibraryCustomNodeFactory
import com.bumble.appyx.sample.hilt.library.onlyinternaldeps.OnlyInternalDepsLibraryNode

class DaggerAppNode(
    buildContext: BuildContext,
    private val aggregateNodeFactory: AggregateNodeFactory = buildContext.getAggregateNodeFactory(),
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = OnlyInternalDependencies,
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<DaggerAppNode.NavTarget>(
    buildContext = buildContext,
    navModel = backStack
) {
    sealed class NavTarget {
        object OnlyInternalDependencies : NavTarget()
        object ExternalDependencies : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is OnlyInternalDependencies -> {
                aggregateNodeFactory.onlyInternalDepsLibraryNodeFactory.create(buildContext)
            }

            is ExternalDependencies -> {
                aggregateNodeFactory.externalDepsLibraryNodeFactory.create(buildContext)
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier) {
            Button(
                onClick = { backStack.replace(OnlyInternalDependencies) },
                content = { Text("Only Internal Dependencies") }
            )
            Button(
                onClick = {
                    backStack.replace(ExternalDependencies)
                },
                content = { Text("External Dependencies") }
            )
            Children(
                modifier = Modifier.padding(16.dp),
                navModel = backStack
            )
        }
    }

    @HiltAggregateNodeFactory
    interface AggregateNodeFactory {
        val onlyInternalDepsLibraryNodeFactory: NodeFactory<OnlyInternalDepsLibraryNode>
        val externalDepsLibraryNodeFactory: ExternalDepsLibraryCustomNodeFactory
    }
}
