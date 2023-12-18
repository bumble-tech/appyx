package com.bumble.appyx.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.gesture.GestureValidator
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.node.ParentNode
import kotlin.math.roundToInt


internal val defaultExtraTouch = 48.dp

@Composable
fun <InteractionTarget : Any, ModelState : Any> ParentNode<InteractionTarget>.AppyxComponent(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureValidator: GestureValidator = GestureValidator.permissiveValidator,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    isGestureBoundingBoxTransformed: Boolean = false,
    decorator: @Composable (child: ChildRenderer, element: Element<InteractionTarget>) -> Unit = { child, _ ->
        child()
    }
) {
    val density = LocalDensity.current
    val screenWidthPx = (LocalScreenSize.current.widthDp * density.density).value.roundToInt()
    val screenHeightPx = (LocalScreenSize.current.heightDp * density.density).value.roundToInt()

    com.bumble.appyx.interactions.core.AppyxComponent(
        appyxComponent,
        screenWidthPx,
        screenHeightPx,
        modifier,
        clipToBounds,
        gestureValidator,
        gestureExtraTouchArea,
        isGestureBoundingBoxTransformed
    ) { element ->
        Child(element, decorator)
    }
}
