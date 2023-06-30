import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.components.backstack.ui.parallax.BackstackParallax
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent


@Suppress("UNCHECKED_CAST")
@Composable
internal fun BackStackParallaxSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember {
        BackStackModel<InteractionTarget>(
            initialTargets = List(5) { InteractionTarget.Element() },
            savedStateMap = null,
        )
    }
    val backStack =
        BackStack(
            scope = coroutineScope,
            model = model,
            motionController = { BackstackParallax(it) },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
        )
    val actions = mapOf(
        "Pop" to {
            backStack.pop()
                 },
        "Push" to {
            backStack.push(InteractionTarget.Element())
        }
    )
    AppyxSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        appyxComponent = backStack as BaseAppyxComponent<InteractionTarget, Any>,
        actions = actions,
        childSize = ChildSize.MAX,
        modifier = modifier,
    )
}
