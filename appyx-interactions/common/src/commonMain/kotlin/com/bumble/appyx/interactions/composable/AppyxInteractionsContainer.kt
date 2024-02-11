package com.bumble.appyx.interactions.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.gesture.GestureValidator
import com.bumble.appyx.interactions.gesture.GestureValidator.Companion.defaultValidator
import com.bumble.appyx.interactions.gesture.detectDragGesturesOrCancellation
import com.bumble.appyx.interactions.gesture.onPointerEvent
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.model.removedElements
import com.bumble.appyx.interactions.ui.LocalBoxScope
import com.bumble.appyx.interactions.ui.LocalMotionProperties
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.output.ElementUiModel
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.property.motionPropertyRenderValue
import com.bumble.appyx.interactions.gesture.GestureReferencePoint

private val defaultExtraTouch = 48f.dp

@Suppress("LongMethod")
@Composable
fun <InteractionTarget : Any, ModelState : Any> AppyxInteractionsContainer(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureValidator: GestureValidator = defaultValidator,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    gestureRelativeTo: GestureReferencePoint = GestureReferencePoint.Container,
    elementUi: @Composable BoxScope.(Element<InteractionTarget>) -> Unit = { _ ->
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "Customise this composable",
        )
    },
) {
    val density = LocalDensity.current
    val elementUiModels by appyxComponent.uiModels.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val gestureExtraTouchAreaPx = with(density) { gestureExtraTouchArea.toPx() }
    val containerSize = remember { mutableStateOf(IntSize.Zero) }
    val saveableStateHolder = rememberSaveableStateHolder()

    LaunchedEffect(appyxComponent) {
        appyxComponent
            .removedElements()
            .collect { deletedElements ->
                deletedElements.forEach {
                    saveableStateHolder.removeState(it)
                }
            }
    }

    SideEffect {
        appyxComponent.updateContext(
            UiContext(
                coroutineScope = coroutineScope,
                clipToBounds = clipToBounds
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (clipToBounds) Modifier.clipToBounds() else Modifier)
            .onPlaced {
                containerSize.value = it.size
                appyxComponent.updateBounds(
                    TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx
                    )
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

                val isGesturesEnabled = appyxComponent.isGesturesEnabled

                key(elementUiModel.element.id) {
                    val isVisible by elementUiModel.visibleState.collectAsState()
                    elementUiModel.persistentContainer()
                    saveableStateHolder.SaveableStateProvider(key = elementUiModel.element) {
                        if (isVisible) {
                            CompositionLocalProvider(
                                LocalMotionProperties provides elementUiModel.motionProperties,
                            ) {
                                when {
                                    !isGesturesEnabled -> {
                                        Box(modifier = elementUiModel.modifier) {
                                            elementUi(elementUiModel.element)
                                        }
                                    }

                                    gestureRelativeTo == GestureReferencePoint.Element ->
                                        ElementWithGestureTransformedBoundingBox(
                                            appyxComponent = appyxComponent,
                                            containerSize = containerSize,
                                            gestureExtraTouchAreaPx = gestureExtraTouchAreaPx,
                                            gestureValidator = gestureValidator,
                                            elementUiModel = elementUiModel,
                                            elementUi = elementUi
                                        )

                                    gestureRelativeTo == GestureReferencePoint.Container ->
                                        ElementWithGesture(
                                            appyxComponent = appyxComponent,
                                            elementUiModel = elementUiModel,
                                            elementUi = elementUi
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun <InteractionTarget : Any, ModelState : Any> ElementWithGesture(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    elementUiModel: ElementUiModel<InteractionTarget>,
    elementUi: @Composable BoxScope.(Element<InteractionTarget>) -> Unit
) {
    val density = LocalDensity.current
    Box(modifier = Modifier
        .pointerInput(appyxComponent) {
            detectDragGesturesOrCancellation(
                onDragStart = { position ->
                    appyxComponent.onStartDrag(position)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    appyxComponent.onDrag(
                        dragAmount,
                        density
                    )
                    true
                },
                onDragEnd = {
                    appyxComponent.onDragEnd()
                }
            )
        }
        .then(elementUiModel.modifier)
    ) {
        elementUi(elementUiModel.element)
    }
}

@Composable
private fun <InteractionTarget : Any, ModelState : Any> ElementWithGestureTransformedBoundingBox(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    containerSize: State<IntSize>,
    gestureExtraTouchAreaPx: Float,
    gestureValidator: GestureValidator,
    elementUiModel: ElementUiModel<InteractionTarget>,
    elementUi: @Composable BoxScope.(Element<InteractionTarget>) -> Unit
) {
    var transformedBoundingBox by remember(elementUiModel.element.id) { mutableStateOf(Rect.Zero) }
    var elementSize by remember(elementUiModel.element.id) { mutableStateOf(IntSize.Zero) }
    var offsetCenter by remember(elementUiModel.element.id) { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val elementOffset =
        offsetCenter.round() - elementOffset(elementSize, containerSize)

    Box(modifier = Modifier
        .offset { elementOffset }
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
                        appyxComponent.onDrag(
                            dragAmount,
                            density
                        )
                        true
                    } else {
                        appyxComponent.onDragEnd()
                        false
                    }
                },
                onDragEnd = {
                    appyxComponent.onDragEnd()
                }
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
                it.boundsInParent()
                    .inflate(gestureExtraTouchAreaPx)
            offsetCenter =
                transformedBoundingBox.center - localCenter
        }
    ) {
        elementUi(elementUiModel.element)
    }
}

@Composable
fun elementOffset(
    elementSize: IntSize,
    containerSize: State<IntSize>,
): IntOffset {
    val positionAlignment =
        motionPropertyRenderValue<PositionAlignment.Value, PositionAlignment>()
    val layoutDirection = LocalLayoutDirection.current

    val alignmentOffset = positionAlignment?.let {
        it.align(elementSize, containerSize.value, layoutDirection)
    } ?: IntOffset.Zero

    return alignmentOffset
}
