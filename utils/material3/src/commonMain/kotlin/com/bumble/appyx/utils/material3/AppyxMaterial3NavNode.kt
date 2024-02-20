package com.bumble.appyx.utils.material3

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.interactions.plugin.Plugin
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.navigation.Appyx
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.children.ChildEntry
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.integration.ScreenSize.WindowSizeClass.COMPACT
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.EmptyNodeView
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.NodeView

@OptIn(ExperimentalMaterial3Api::class)
open class AppyxMaterial3NavNode<NavTarget : Any>(
    nodeContext: NodeContext,
    private val navTargets: List<NavTarget>,
    private val navTargetResolver: (NavTarget) -> AppyxNavItem,
    private val initialActiveElement: NavTarget = navTargets.first(),
    private val animationSpec: SpringSpec<Float> = spring(
        stiffness = Spring.StiffnessHigh
    ),
    private val visualisation: (UiContext) -> Visualisation<NavTarget, State<NavTarget>> = {
        SpotlightFader(
            uiContext = it,
            defaultAnimationSpec = animationSpec
        )
    },
    protected val spotlight: Spotlight<NavTarget> = Spotlight(
        model = SpotlightModel(
            items = navTargets,
            initialActiveIndex = navTargets.indexOf(initialActiveElement).toFloat(),
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = visualisation
    ),
    view: NodeView = EmptyNodeView(),
    childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    childAware: ChildAware<Node<NavTarget>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : Node<NavTarget>(
    appyxComponent = spotlight,
    nodeContext = nodeContext,
    view = view,
    childKeepMode = childKeepMode,
    childAware = childAware,
    plugins = plugins
) {

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        navTargetResolver
            .invoke(navTarget)
            .node
            .invoke(nodeContext)

    @Composable
    override fun Content(modifier: Modifier) {
        val selectedIndex = spotlight.activeIndex.collectAsState().value.toInt()
        val screenSize = LocalScreenSize.current
        var containerSize by remember { mutableStateOf(screenSize) }
        val density = LocalDensity.current

        Scaffold(
            modifier = Modifier
                .onPlaced {
                    containerSize = with(density) {
                        ScreenSize(
                            it.size.width.toDp(),
                            it.size.height.toDp()
                        )
                    }
                },
            bottomBar = {
                if (shouldUseNavigationBar(containerSize)) {
                    NavigationBar(selectedIndex)
                }
            },
            content = { paddingValues: PaddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Row {
                        if (shouldUseNavigationRail(containerSize)) {
                            NavigationRail(selectedIndex)
                        }
                        CurrentNavItem()
                    }
                }
            }
        )
    }

    @Composable
    open fun shouldUseNavigationBar(screenSize: ScreenSize): Boolean =
        screenSize.windowSizeClass == COMPACT

    @Composable
    open fun shouldUseNavigationRail(size: ScreenSize): Boolean =
        !shouldUseNavigationBar(size)

    @Composable
    fun CurrentNavItem() {
        AppyxNavigationContainer(
            appyxComponent = spotlight
        )
    }

    @Composable
    fun NavigationBar(selectedIndex: Int) {
        NavigationBar {
            navTargets.forEachIndexed { index, item ->
                val appyxNavItem = navTargetResolver.invoke(item)

                NavigationBarItem(
                    icon = { appyxNavItem.icon(selectedIndex == index) },
                    label = { appyxNavItem.text(selectedIndex == index) },
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }

    @Composable
    fun NavigationRail(selectedIndex: Int, modifier: Modifier = Modifier) {
        NavigationRail(modifier = modifier.zIndex(Float.MAX_VALUE)) {
            navTargets.forEachIndexed { index, item ->
                val appyxNavItem = navTargetResolver.invoke(item)

                NavigationRailItem(
                    icon = { appyxNavItem.icon(selectedIndex == index) },
                    label = { appyxNavItem.text(selectedIndex == index) },
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }

    private fun onItemSelected(index: Int) {
        spotlight.activate(index.toFloat())
    }
}
