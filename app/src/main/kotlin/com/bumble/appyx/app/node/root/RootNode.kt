package com.bumble.appyx.app.node.root

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.R
import com.bumble.appyx.app.composable.ScreenCenteredContent
import com.bumble.appyx.app.node.helper.screenNode
import com.bumble.appyx.app.node.onboarding.OnboardingContainerNode
import com.bumble.appyx.app.node.root.RootNode.Routing
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.activeRouting
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Splash,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    navModel = backStack,
    buildContext = buildContext
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Splash : Routing()

        @Parcelize
        object Onboarding : Routing()

        @Parcelize
        object Main : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Routing.Splash -> screenNode(buildContext) { Splash() }
            Routing.Onboarding -> OnboardingContainerNode(buildContext)
            Routing.Main -> screenNode(buildContext) { Text(text = "Main") }
        }

    override fun onChildFinished(child: Node) {
        when (child) {
            is OnboardingContainerNode -> backStack.newRoot(Routing.Main)
            else -> super.onChildFinished(child)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        LaunchedEffect(backStack) {
            if (backStack.activeRouting == Routing.Splash) {
                delay(1000)
                backStack.newRoot(Routing.Onboarding)
            }
        }

        Children(
            navModel = backStack,
            transitionHandler = rememberBackstackFader { tween(750) }
        )
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
