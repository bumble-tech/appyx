package com.bumble.appyx.demos.backstack.parallax

import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.parallax.BackstackParallax
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.ChildSize
import com.bumble.appyx.demos.common.InteractionTarget
import com.bumble.appyx.interactions.core.model.BaseInteractionModel

@Composable
fun BackStackParallaxSample(
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
            gestureFactory = { BackstackParallax.Gestures(it) },
            animationSpec = spring(stiffness = StiffnessVeryLow),
        )
    val actions = mapOf(
        "Pop" to { backStack.pop() },
        "Push" to { backStack.push(InteractionTarget.Element()) }
    )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        interactionModel = backStack.unsafeCast<BaseInteractionModel<InteractionTarget, Any>>(),
        actions = actions,
        childSize = ChildSize.MAX,
        modifier = modifier,
    )
}