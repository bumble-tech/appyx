package com.bumble.appyx.sample.hilt.library.externaldeps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.bumble.appyx.sample.hilt.library.InternalDependency
import com.bumble.appyx.sample.hilt.library.externaldeps.ExternalDepsLibraryNode.NavTarget
import com.bumble.appyx.sample.hilt.library.externaldeps.ExternalDepsLibraryNode.NavTarget.NoDependencies
import com.bumble.appyx.sample.hilt.library.nodeps.NoDepsLibraryNode

class ExternalDepsLibraryNode internal constructor(
    buildContext: BuildContext,
    private val dependency: InternalDependency,
    private val aggregateNodeFactory: AggregateNodeFactory = buildContext.getAggregateNodeFactory(),
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NoDependencies,
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack
) {
    sealed class NavTarget {
        object NoDependencies : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NoDependencies -> aggregateNodeFactory.noDepsLibraryNodeFactory.create(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier) {
            Text("External dependencies node. Activity: ${dependency.getActivityName()}")
            Children(
                modifier = Modifier.padding(16.dp),
                navModel = backStack
            )
        }
    }

    @HiltAggregateNodeFactory
    interface AggregateNodeFactory {
        val noDepsLibraryNodeFactory: NodeFactory<NoDepsLibraryNode>
    }
}
