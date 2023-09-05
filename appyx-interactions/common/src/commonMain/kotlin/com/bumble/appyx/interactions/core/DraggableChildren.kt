package com.bumble.appyx.interactions.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.bumble.appyx.interactions.core.gesture.GestureValidator
import com.bumble.appyx.interactions.core.gesture.GestureValidator.Companion.defaultValidator
import com.bumble.appyx.interactions.core.gesture.detectDragGesturesOrCancellation
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.modifiers.onPointerEvent
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside.Value
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue

private val defaultExtraTouch = 48f.dp


@Suppress("LongMethod")
@Composable
fun <InteractionTarget : Any, ModelState : Any> DraggableAppyxComponent(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureValidator: GestureValidator = defaultValidator,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    element: @Composable (ElementUiModel<InteractionTarget>) -> Unit = { elementUiModel ->
        Box(
            modifier = Modifier.fillMaxSize()
                .then(elementUiModel.modifier)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Customise this composable",
            )
        }
    },
) {
    val density = LocalDensity.current
    val elementUiModels by appyxComponent.uiModels.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val gestureExtraTouchAreaPx = with(density) { gestureExtraTouchArea.toPx() }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var uiContext by remember { mutableStateOf<UiContext?>(null) }

    LaunchedEffect(uiContext) {
        uiContext?.let { appyxComponent.updateContext(it) }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (clipToBounds) Modifier.clipToBounds() else Modifier)
            .onPlaced {
                containerSize = it.size
                uiContext = UiContext(
                    coroutineScope = coroutineScope,
                    transitionBounds = TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    ),
                    clipToBounds = clipToBounds
                )
            }
            .onPointerEvent {
                if (it.type == PointerEventType.Release) {
                    appyxComponent.onRelease()
                }
            }
    ) {
        CompositionLocalProvider(LocalBoxScope provides this) {
            elementUiModels.forEach { elementUiModel ->
                val id = elementUiModel.element.id

                key(id) {
                    var transformedBoundingBox by remember(id) { mutableStateOf(Rect.Zero) }
                    var elementSize by remember(id) { mutableStateOf(IntSize.Zero) }
                    var offsetCenter by remember(id) { mutableStateOf(Offset.Zero) }
                    val isVisible by elementUiModel.visibleState.collectAsState()
                    
                    elementUiModel.persistentContainer()

                    if (isVisible) {
                        CompositionLocalProvider(
                            LocalMotionProperties provides elementUiModel.motionProperties
                        ) {
                            val elementOffset = offsetCenter.round() - elementOffset(elementSize, containerSize)

                            element.invoke(
                                elementUiModel.copy(
                                    modifier = Modifier
                                        .offset { elementOffset }
                                        .width(with(density) { elementSize.width.toDp() })
                                        .height(with(density) { elementSize.height.toDp() })
                                        .pointerInput(appyxComponent) {
                                            detectDragGesturesOrCancellation(
                                                onDragStart = { position ->
                                                    appyxComponent.onStartDrag(position)
                                                },
                                                onDrag = { change, dragAmount ->
                                                    if (gestureValidator.isGestureValid(
                                                            change.position,
                                                            transformedBoundingBox.translate(-offsetCenter)
                                                        )
                                                    ) {
                                                        change.consume()
                                                        appyxComponent.onDrag(dragAmount, density)
                                                        true
                                                    } else {
                                                        appyxComponent.onDragEnd()
                                                        false
                                                    }
                                                },
                                                onDragEnd = {
                                                    appyxComponent.onDragEnd()
                                                },
                                            )
                                        }
                                        .offset { -elementOffset }
                                        .then(elementUiModel.modifier)
                                        .onPlaced {
                                            elementSize = it.size
                                            val localCenter = Offset(
                                                it.size.width.toFloat(),
                                                it.size.height.toFloat()
                                            ) / 2f

                                            transformedBoundingBox =
                                                it.boundsInParent().inflate(gestureExtraTouchAreaPx)
                                            offsetCenter =
                                                transformedBoundingBox.center - localCenter
                                        }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun elementOffset(
    elementSize: IntSize,
    containerSize: IntSize,
): IntOffset {

    val positionInside = motionPropertyRenderValue<Value, PositionInside>()
    val positionOutside = motionPropertyRenderValue<PositionOutside.Value, PositionOutside>()
    val layoutDirection = LocalLayoutDirection.current

    val positionInsideOffset by derivedStateOf {
        positionInside?.let {
            it.alignment.align(elementSize, containerSize, layoutDirection)
        } ?: IntOffset.Zero
    }
    val positionOutsideOffset by derivedStateOf {
        positionOutside?.let {
            it.alignment.align(elementSize, containerSize, layoutDirection)
        } ?: IntOffset.Zero
    }

    return positionInsideOffset - positionOutsideOffset
}
