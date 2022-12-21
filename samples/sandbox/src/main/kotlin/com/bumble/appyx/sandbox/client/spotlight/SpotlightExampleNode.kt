package com.bumble.appyx.sandbox.client.spotlight

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.backpresshandler.GoToPrevious
import com.bumble.appyx.navmodel.spotlight.current
import com.bumble.appyx.navmodel.spotlight.elementsCount
import com.bumble.appyx.navmodel.spotlight.hasNext
import com.bumble.appyx.navmodel.spotlight.hasPrevious
import com.bumble.appyx.navmodel.spotlight.operation.activate
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.operation.previous
import com.bumble.appyx.navmodel.spotlight.operation.updateElements
import com.bumble.appyx.sandbox.client.child.ChildNode
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode.NavTarget.Child1
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode.NavTarget.Child2
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode.NavTarget.Child3
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode.ScreenState.Loaded
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode.ScreenState.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Shows how to use spotlight to create a UI with BottomTabBar
 */
class SpotlightExampleNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<NavTarget> = Spotlight(
        items = emptyList(),
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = GoToPrevious(),
    )
) : ParentNode<SpotlightExampleNode.NavTarget>(
    buildContext = buildContext,
    navModel = spotlight
) {

    private val screenState = mutableStateOf<ScreenState?>(null)

    sealed class ScreenState {
        object Loading : ScreenState()
        object Loaded : ScreenState()
    }

    init {
        // simulate loading tabs
        if (spotlight.elementsCount() == 0) {
            screenState.value = Loading
            lifecycle.coroutineScope.launch {
                delay(1000)
                spotlight.updateElements(items = Tab.getTabList())
                screenState.value = Loaded
            }
        } else {
            screenState.value = Loaded
        }
    }

    sealed class NavTarget : Parcelable {

        @Parcelize
        object Child1 : NavTarget()

        @Parcelize
        object Child2 : NavTarget()

        @Parcelize
        object Child3 : NavTarget()
    }

    @Parcelize
    private enum class Tab(val navTarget: NavTarget) : Parcelable {
        C1(Child1),
        C2(Child2),
        C3(Child3);

        companion object {
            fun getTabList() = values().map { it.navTarget }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            Child1 -> ChildNode(name = "Child1", buildContext = buildContext)
            Child2 -> ChildNode(name = "Child2", buildContext = buildContext)
            Child3 -> ChildNode(name = "Child3", buildContext = buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val state by screenState

        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is Loading -> CircularProgressIndicator()
                is Loaded -> LoadedState()
                else -> Unit
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("LongMethod")
    @Composable
    private fun LoadedState(modifier: Modifier = Modifier) {
        val hasPrevious = spotlight.hasPrevious().collectAsState(initial = false)
        val hasNext = spotlight.hasNext().collectAsState(initial = false)
        val currentTab = spotlight.current().collectAsState(initial = null)
        Scaffold(
            modifier = modifier.fillMaxSize(),
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = { PageButtons(hasPrevious.value, hasNext.value) },
            bottomBar = {
                BottomTabs(currentTab)
            }
        ) {
            Children(
                modifier = Modifier
                    .padding(it),
                transitionHandler = rememberSpotlightFaderThrough(),
                navModel = spotlight
            )
        }
    }

    @Composable
    private fun BottomTabs(currentTab: State<NavTarget?>) {
        NavigationBar {
            Tab.values().forEach { tab ->
                val selected = currentTab.value == tab.navTarget
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selected)
                                Icons.Filled.Favorite
                            else
                                Icons.Outlined.FavoriteBorder,
                            contentDescription = tab.toString()
                        )
                    },
                    label = { Text(tab.toString()) },
                    selected = selected,
                    onClick = { spotlight.activate(tab) }
                )
            }
        }
    }

    @Composable
    private fun PageButtons(
        hasPrevious: Boolean,
        hasNext: Boolean
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = { spotlight.previous() },
                modifier = if (hasPrevious) Modifier.shadow(
                    4.dp,
                    IconButtonDefaults.filledShape
                ) else Modifier,
                enabled = hasPrevious,
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }
            FilledIconButton(
                onClick = { spotlight.next() },
                modifier = if (hasNext) Modifier.shadow(
                    4.dp,
                    IconButtonDefaults.filledShape
                ) else Modifier,
                enabled = hasNext,
            ) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }
    }

    private fun Spotlight<*>.activate(item: Tab) {
        activate(item.ordinal)
    }
}
