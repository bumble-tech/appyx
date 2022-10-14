package com.bumble.appyx.app.node.root

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.content.edit
import com.bumble.appyx.R
import com.bumble.appyx.app.composable.ScreenCenteredContent
import com.bumble.appyx.app.node.helper.screenNode
import com.bumble.appyx.app.node.onboarding.OnboardingContainerNode
import com.bumble.appyx.app.node.root.RootNode.NavTarget
import com.bumble.appyx.app.node.samples.SamplesContainerNode
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.Splash,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Splash : NavTarget()

        @Parcelize
        object Onboarding : NavTarget()

        @Parcelize
        object Samples : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            NavTarget.Splash -> screenNode(buildContext) { Splash() }
            NavTarget.Onboarding -> OnboardingContainerNode(buildContext)
            NavTarget.Samples -> SamplesContainerNode(buildContext)
        }

    override fun onChildFinished(child: Node) {
        when (child) {
            is OnboardingContainerNode -> backStack.newRoot(NavTarget.Samples)
            else -> super.onChildFinished(child)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val context: Context = LocalContext.current
        LaunchedEffect(backStack) {
            if (backStack.activeElement == NavTarget.Splash) {
                delay(1000)
                launchNewRootScreen(context)
            }
        }

        Children(
            modifier = modifier,
            navModel = backStack,
            transitionHandler = rememberBackstackFader { tween(750) }
        )
    }

    /**
     * We only want to show onboarding once (though the user can navigate back to it manually).
     * This will allow them to look at samples quicker on subsequent uses.
     */
    private fun launchNewRootScreen(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val onboardingShown = preferences.getBoolean(ONBOARDING_SHOWN_KEY, false)
        if (!onboardingShown) {
            preferences.edit { putBoolean(ONBOARDING_SHOWN_KEY, true) }
            backStack.newRoot(NavTarget.Onboarding)
        } else {
            backStack.newRoot(NavTarget.Samples)
        }
    }

    private companion object {
        private const val PREFERENCES_NAME = "sample_preferences"
        private const val ONBOARDING_SHOWN_KEY = "onboarding_shown"
    }
}

@Preview
@Composable
fun Splash() {
    ScreenCenteredContent {
        val image: Painter = painterResource(
            id = if (isSystemInDarkTheme()) R.drawable.appyx_text_white_s else R.drawable.appyx_text_black_s
        )
        Image(
            painter = image,
            contentDescription = "logo",
            modifier = Modifier.fillMaxSize(0.65f)
        )
    }
}
