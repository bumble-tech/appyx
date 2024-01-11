package com.bumble.appyx.demos.backstack.stack3d

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
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.ChildSize
import com.bumble.appyx.demos.common.InteractionTarget

@Composable
fun BackStack3DSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember {
        BackStackModel<InteractionTarget>(
            initialTarget = InteractionTarget.Element(),
            savedStateMap = null
        )
    }
    val backStack =
        BackStack(
            scope = coroutineScope,
            model = model,
            visualisation = { BackStack3D(it) },
            gestureFactory = { BackStack3D.Gestures(it) },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow * 2),
        )
    val actions = mapOf(
        "Pop" to { backStack.pop() },
        "Push" to { backStack.push(InteractionTarget.Element()) }
    )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        appyxComponent = backStack,
        actions = actions,
        childSize = ChildSize.MEDIUM,
        modifier = modifier,
    )
}
