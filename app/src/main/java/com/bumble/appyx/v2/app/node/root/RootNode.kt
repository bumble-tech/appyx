package com.bumble.appyx.v2.app.node.root

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.v2.app.node.helper.screenNode
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.Output.FinishedOnboarding
import com.bumble.appyx.v2.app.node.root.RootNode.Routing
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.lifecycle.subscribe
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.newRoot
import com.bumble.appyx.v2.core.routing.source.backstack.rememberBackstackFader
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Splash,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
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
            Routing.Splash -> screenNode(buildContext) { Text(text = "Splash") }
            Routing.Onboarding -> OnboardingContainerNode(buildContext)
            Routing.Main -> screenNode(buildContext) { Text(text = "Main") }
        }

    override fun onBuilt() {
        super.onBuilt()
        whenChildAttached(OnboardingContainerNode::class) { lifecycle, onboarding ->
            val onboardingOutput = onboarding.output.subscribe {
                when (it) {
                    FinishedOnboarding -> backStack.newRoot(Routing.Main)
                }
            }
            lifecycle.subscribe(
                onDestroy = { onboardingOutput.dispose() }
            )
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        LaunchedEffect(backStack.routings) {
            if (backStack.routings.value == listOf(Routing.Splash)) {
                delay(750)
                backStack.newRoot(Routing.Onboarding)
            }
        } 

        Children(
            modifier = modifier,
            routingSource = backStack,
            transitionHandler = rememberBackstackFader { tween(750) }
        ) {
            children<Routing> { child ->
                child()
            }
        }
    }
}
