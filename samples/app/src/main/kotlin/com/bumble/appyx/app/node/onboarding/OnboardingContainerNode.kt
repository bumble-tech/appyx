package com.bumble.appyx.app.node.onboarding

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.composable.SpotlightDotsIndicator
import com.bumble.appyx.app.node.onboarding.OnboardingContainerNode.NavTarget
import com.bumble.appyx.app.node.onboarding.screen.modeldriven.ComposableNavigation
import com.bumble.appyx.app.node.onboarding.screen.modeldriven.Intro
import com.bumble.appyx.app.node.onboarding.screen.modeldriven.ModelDrivenIntro
import com.bumble.appyx.app.node.onboarding.screen.modeldriven.NavModelTeaserNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.app.ui.appyx_dark
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.backpresshandler.GoToPrevious
import com.bumble.appyx.navmodel.spotlight.hasNext
import com.bumble.appyx.navmodel.spotlight.hasPrevious
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.operation.previous
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightSlider
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class OnboardingContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<NavTarget> = Spotlight(
        items = listOf(
            NavTarget.Intro,
            NavTarget.ModelDrivenIntro,
            NavTarget.NavModelTeaser,
            NavTarget.ComposableNavigation,
        ),
        backPressHandler = GoToPrevious(),
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<NavTarget>(
    navModel = spotlight,
    buildContext = buildContext
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Intro : NavTarget()
        @Parcelize
        object ModelDrivenIntro : NavTarget()

        @Parcelize
        object NavModelTeaser : NavTarget()

        @Parcelize
        object ComposableNavigation : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            NavTarget.Intro -> Intro(buildContext)
            NavTarget.ModelDrivenIntro -> ModelDrivenIntro(buildContext)
            NavTarget.NavModelTeaser -> NavModelTeaserNode(buildContext)
            NavTarget.ComposableNavigation -> ComposableNavigation(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val hasPrevious = spotlight.hasPrevious().collectAsState(initial = false)
        val hasNext = spotlight.hasNext().collectAsState(initial = false)
        val previousVisibility = animateFloatAsState(
            targetValue = if (hasPrevious.value) 1f else 0f
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
        ) {
            Children(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                transitionHandler = rememberSpotlightSlider(clipToBounds = true),
                navModel = spotlight
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                SpotlightDotsIndicator(
                    spotlight = spotlight
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (hasNext.value) {
                        PreviousAndNextButtons(
                            previousVisibility = previousVisibility,
                            hasPrevious = hasPrevious,
                        )
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { finish() }
                        ) {
                            Text(
                                text = "Check it out!",
                                color = appyx_dark,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.PreviousAndNextButtons(
        previousVisibility: State<Float>,
        hasPrevious: State<Boolean>,
    ) {
        TextButton(
            modifier = Modifier.alpha(previousVisibility.value),
            enabled = hasPrevious.value,
            onClick = { spotlight.previous() }
        ) {
            Text(
                text = "Previous".toUpperCase(Locale.current),
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(
            onClick = { spotlight.next() }
        ) {
            Text(
                text = "Next".toUpperCase(Locale.current),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun OnboardingContainerNodePreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun OnboardingContainerNodePreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                OnboardingContainerNode(root(null))
            }
        }
    }
}

