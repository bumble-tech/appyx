package com.bumble.appyx.interactions.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.gesture.detectDragGesturesOrCancellation
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel

@Composable
fun <InteractionTarget : Any, ModelState : Any> DraggableChildren(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset, elementBoundingBox: Rect) -> Boolean,
    element: @Composable (ElementUiModel<InteractionTarget>) -> Unit,
) {
    val density = LocalDensity.current
    val elementUiModels = interactionModel.uiModels.collectAsState(listOf())
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
        var elementBoundingBox by remember { mutableStateOf(Rect.Zero) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(interactionModel) {
                    detectDragGesturesOrCancellation(
                        onDragStart = onDragStart,
                        onDrag = { change, dragAmount -> onDrag(change, dragAmount, elementBoundingBox) },
                        onDragEnd = onDragEnd,
                    )
                }
        ) {
            elementUiModels.value.forEach { elementUiModel ->
                key(elementUiModel.element.id) {
                    elementUiModel.animationContainer()
                    val isVisible by elementUiModel.visibleState.collectAsState()
                    if (isVisible) {
                        element.invoke(
                            elementUiModel.copy(
                                modifier = elementUiModel.modifier.onPlaced {
                                    elementBoundingBox = it.boundsInParent()
                                        .run { inflate(maxDimension) }
                                }
                            ))
                    }
                }
            }
        }
    }
}
