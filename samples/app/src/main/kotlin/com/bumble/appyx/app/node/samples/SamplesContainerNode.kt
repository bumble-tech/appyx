package com.bumble.appyx.app.node.samples

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.node.cards.CardsExampleNode
import com.bumble.appyx.app.node.helper.screenNode
import com.bumble.appyx.app.node.slideshow.WhatsAppyxSlideShow
import com.bumble.appyx.core.composable.ChildRenderer
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sample.navigtion.compose.ComposeNavigationRoot
import kotlinx.parcelize.Parcelize

class SamplesContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.SamplesListScreen,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<SamplesContainerNode.NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {

    sealed class NavTarget : Parcelable {
        open val showBackButton: Boolean = true

        @Parcelize
        object SamplesListScreen : NavTarget() {
            override val showBackButton: Boolean
                get() = false
        }

        @Parcelize
        data class OnboardingScreen(val autoAdvance: Boolean = false) : NavTarget() {
            override val showBackButton: Boolean
                get() = false
        }

        @Parcelize
        object ComposeNavigationScreen : NavTarget()

        @Parcelize
        object CardsExample : NavTarget()
    }

    @ExperimentalUnitApi
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.SamplesListScreen -> screenNode(buildContext) { SamplesSelector(backStack) }
            NavTarget.CardsExample -> CardsExampleNode(buildContext)
            is NavTarget.OnboardingScreen -> WhatsAppyxSlideShow(
                buildContext = buildContext,
                autoAdvanceDelayMs = if (navTarget.autoAdvance) 2500 else null
            )
            is NavTarget.ComposeNavigationScreen -> {
                node(buildContext) {
                    // compose-navigation fetches the integration point via LocalIntegrationPoint
                    CompositionLocalProvider(
                        LocalIntegrationPoint provides integrationPoint,
                    ) {
                        ComposeNavigationRoot()
                    }
                }
            }
        }

    @ExperimentalUnitApi
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun onChildFinished(child: Node) {
        when (child) {
            is WhatsAppyxSlideShow -> backStack.newRoot(NavTarget.SamplesListScreen)
            else -> super.onChildFinished(child)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val elementsState by backStack.elements.collectAsState()

        if (elementsState.activeElement?.showBackButton == true) {
            IconButton(onClick = { backStack.pop() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Children(
                modifier = Modifier.fillMaxSize(),
                transitionHandler = rememberBackstackSlider(),
                navModel = backStack
            )
        }
    }

    @Composable
    fun SamplesSelector(backStack: BackStack<NavTarget>, modifier: Modifier = Modifier) {
        val decorator: @Composable (child: ChildRenderer) -> Unit = remember {
            {
                ScaledLayout() {
                    it.invoke()
                }

            }
        }
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)

        ) {
            item {
                SampleItem(
                    title = "Dating cards NavModel",
                    subtitle = "Swipe right on the NavModel concept",
                    onClick = { backStack.push(NavTarget.CardsExample) },
                ) { PermanentChild(navTarget = NavTarget.CardsExample, decorator = decorator) }
            }
            item {
                SampleItem(
                    title = "What is Appyx?",
                    subtitle = "Explore some of the main ideas of Appyx in a set of slides",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16f / 9),
                    onClick = { backStack.push(NavTarget.OnboardingScreen(false)) },
                ) { PermanentChild(navTarget = NavTarget.OnboardingScreen(true), decorator = decorator) }
            }
            item {
                SampleItem(
                    title = "Compose Navigation",
                    subtitle = "See Appyx nodes interact with Jetpack Compose Navigation library",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16f / 9),
                    onClick = { backStack.push(NavTarget.ComposeNavigationScreen) },
                ) {
                    PermanentChild(
                        navTarget = NavTarget.ComposeNavigationScreen,
                        decorator = decorator
                    )
                }
            }
        }
    }
}

