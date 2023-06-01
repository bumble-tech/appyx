package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel


@Composable
fun <InteractionTarget : Any, ModelState : Any> Children(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    element: @Composable (ElementUiModel<InteractionTarget>) -> Unit = {
        Element(colors = colors, elementUiModel = it)
    },
) {
    val density = LocalDensity.current
    val frames = interactionModel.uiModels.collectAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    var uiContext by remember { mutableStateOf<UiContext?>(null) }

    LaunchedEffect(uiContext) {
        uiContext?.let { interactionModel.updateContext(it) }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (clipToBounds) Modifier.clipToBounds() else Modifier)
            .onPlaced {
                uiContext = UiContext(
                    coroutineScope = coroutineScope,
                    transitionBounds = TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        containerBoundsInRoot = it.boundsInRoot(),
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    ),
                    clipToBounds = clipToBounds
                )
            }
    ) {
        frames.value.forEach { frameModel ->
            key(frameModel.element.id) {
                frameModel.animationContainer()
                val isVisible by frameModel.visibleState.collectAsState()
                if (isVisible) {
                    element.invoke(frameModel)
                }
            }
        }
    }
}

@Composable
fun Element(
    elementUiModel: ElementUiModel<*>,
    modifier: Modifier = Modifier.fillMaxSize(),
    colors: List<Color>,
    color: Color? = Color.Unspecified,
    contentDescription: String? = null
) {
    val backgroundColor = remember {
        if (color == Color.Unspecified) colors.shuffled().random() else color ?: Color.Unspecified
    }

    Box(
        modifier = Modifier
            .then(elementUiModel.modifier)
            .clip(RoundedCornerShape(5))
            .then(if (color == null) Modifier else Modifier.background(backgroundColor))
            .then(modifier)
            .padding(24.dp)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            }
    ) {

        Text(
            text = elementUiModel.element.interactionTarget.toString(),
            fontSize = 21.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}
