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
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.navigation.Appyx
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.children.ChildEntry
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.integration.ScreenSize.WindowSizeClass.COMPACT
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.EmptyParentNodeView
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.ParentNodeView

@OptIn(ExperimentalMaterial3Api::class)
open class AppyxMaterial3NavNode<NavTarget : Any>(
    buildContext: BuildContext,
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
            savedStateMap = buildContext.savedStateMap
        ),
        visualisation = visualisation
    ),
    view: ParentNodeView<NavTarget> = EmptyParentNodeView(),
    childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    childAware: ChildAware<ParentNode<NavTarget>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : ParentNode<NavTarget>(
    appyxComponent = spotlight,
    buildContext = buildContext,
    view = view,
    childKeepMode = childKeepMode,
    childAware = childAware,
    plugins = plugins
) {

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        navTargetResolver
            .invoke(navTarget)
            .node
            .invoke(buildContext)

    @Composable
    override fun View(modifier: Modifier) {
        val selectedIndex = spotlight.activeIndex.collectAsState(-1f).value.toInt()

        Scaffold(
            bottomBar = {
                if (shouldUseNavigationBar()) {
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
                        if (shouldUseNavigationRail()) {
                            NavigationRail(selectedIndex)
                        }
                        CurrentNavItem()
                    }
                }
            }
        )
    }

    @Composable
    open fun shouldUseNavigationBar(): Boolean =
        LocalScreenSize.current.windowSizeClass == COMPACT

    @Composable
    open fun shouldUseNavigationRail(): Boolean =
        !shouldUseNavigationBar()

    @Composable
    fun CurrentNavItem() {
        AppyxComponent(
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
