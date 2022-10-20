package com.bumble.appyx.app.node.samples

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.node.helper.screenNode
import com.bumble.appyx.app.node.onboarding.OnboardingContainerNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
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
        object OnboardingScreen : NavTarget() {
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
            NavTarget.SamplesListScreen -> screenNode(buildContext) { SamplesSelector(backStack) }
            NavTarget.OnboardingScreen -> OnboardingContainerNode(buildContext)
            NavTarget.ComposeNavigationScreen -> {
                node(buildContext) {
                    // compose-navigation fetches the integration point via LocalIntegrationPoint
                    CompositionLocalProvider(
                        LocalIntegrationPoint provides integrationPoint,
                    ) {
                        ComposeNavigationRoot()
                    }
                }
            }

            NavTarget.CardsExample -> CardsExampleNode(buildContext)
        }

    @ExperimentalUnitApi
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun onChildFinished(child: Node) {
        when (child) {
            is OnboardingContainerNode -> backStack.newRoot(NavTarget.SamplesListScreen)
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
    private fun SamplesSelector(backStack: BackStack<NavTarget>) {
        val decorator: @Composable (child: ChildRenderer) -> Unit = remember {
            {
                ScaledLayout() {
                    it.invoke()
                }

            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    onClick = { backStack.push(NavTarget.OnboardingScreen) },
                ) { PermanentChild(navTarget = NavTarget.OnboardingScreen, decorator = decorator) }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SampleItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(16f / 9),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
    ) {
        Row(
            Modifier
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .aspectRatio(9f / 16),
                shape = MaterialTheme.shapes.medium
            ) {
                content()
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Start
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Start
                )
            }
        }
    }

}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SampleItemPreview() {
    AppyxSampleAppTheme {
        SampleItem(
            title = "What is Appyx?",
            subtitle = "Explore some of the main ideas of Appyx in a set of slides",
            onClick = {},
            modifier = Modifier
        ) {}
    }
}
