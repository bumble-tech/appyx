package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.output.FrameModel
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import kotlin.math.roundToInt


@ExperimentalMaterialApi
@Composable
fun <NavTarget : Any, NavState : Any> Children(
    interactionModel: InteractionModel<NavTarget, NavState>,
    modifier: Modifier = Modifier,
    element: @Composable (FrameModel<NavTarget>) -> Unit = {
        Element(frameModel = it)
    },
) {
    val density = LocalDensity.current
    val frames = interactionModel.frames.collectAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val screenWidthPx = (LocalConfiguration.current.screenWidthDp * density.density).roundToInt()
    val screenHeightPx = (LocalConfiguration.current.screenHeightDp * density.density).roundToInt()
    var uiContext by remember { mutableStateOf<UiContext?>(null) }

    LaunchedEffect(uiContext) {
        val uiContext = uiContext
        if (uiContext != null) {
            interactionModel.updateContext(uiContext)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .composed {
                val clipToBounds by interactionModel.clipToBounds.collectAsState(initial = false)
                if (clipToBounds) {
                    clipToBounds()
                } else {
                    this
                }
            }
            .onGloballyPositioned {
                uiContext = UiContext(
                    TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        containerBoundsInRoot = it.boundsInRoot(),
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    ),
                    coroutineScope
                )
            }
    ) {
        frames.value.forEach { frameModel ->
            key(frameModel.element.id) {
                frameModel.animationContainer()
                val isVisible by frameModel.visibleState.collectAsState(initial = false)
                if (isVisible) {
                    element.invoke(frameModel)
                }
            }
        }
    }
}

@Composable
fun Element(
    color: Color? = Color.Unspecified,
    frameModel: FrameModel<*>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val backgroundColor = remember {
        if (color == Color.Unspecified) colors.shuffled().random() else color ?: Color.Unspecified
    }

    Box(
        modifier = Modifier
            .then(frameModel.modifier)
            .clip(RoundedCornerShape(5))
            .then(if (color == null) Modifier else Modifier.background(backgroundColor))
            .then(modifier)
            .padding(24.dp)
    ) {
        Text(
            text = frameModel.element.interactionTarget.toString(),
            fontSize = 21.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}
