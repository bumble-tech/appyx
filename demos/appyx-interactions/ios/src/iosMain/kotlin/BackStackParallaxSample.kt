import SampleInteractionTarget.Element
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
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax

@Composable
internal fun BackStackParallaxSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember {
        BackStackModel<SampleInteractionTarget>(
            initialTargets = List(1) { Element() },
            savedStateMap = null,
        )
    }
    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = model,
            visualisation = { BackStackParallax(it) },
            gestureFactory = { BackStackParallax.Gestures(it) },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
        )
    }
    val actions = mapOf(
        "Pop" to { backStack.pop() },
        "Push" to { backStack.push(Element()) }
    )
    AppyxSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        appyxComponent = backStack,
        actions = actions,
        modifier = modifier,
    )
}
