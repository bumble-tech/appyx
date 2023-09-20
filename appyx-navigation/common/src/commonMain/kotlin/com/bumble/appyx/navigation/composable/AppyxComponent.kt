package com.bumble.appyx.navigation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
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
import com.bumble.appyx.interactions.core.gesture.GestureValidator
import com.bumble.appyx.interactions.core.gesture.detectDragGesturesOrCancellation
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.model.removedElements
import com.bumble.appyx.interactions.core.modifiers.onPointerEvent
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.node.ParentNode
import kotlin.math.roundToInt


internal val defaultExtraTouch = 48.dp

@Composable
fun <InteractionTarget : Any, ModelState : Any> ParentNode<InteractionTarget>.AppyxComponent(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureValidator: GestureValidator = GestureValidator.defaultValidator,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    block: @Composable (ChildrenTransitionScope<InteractionTarget, ModelState>.() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val screenWidthPx = (LocalScreenSize.current.widthDp * density.density).value.roundToInt()
    val screenHeightPx = (LocalScreenSize.current.heightDp * density.density).value.roundToInt()
    val coroutineScope = rememberCoroutineScope()
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val childrenBlock = block ?: {
        children { child, _ ->
            child()
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
                containerSize = it.size
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
        CompositionLocalProvider(LocalBoxScope provides this@Box) {
            childrenBlock(
                ChildrenTransitionScope(containerSize, appyxComponent, gestureExtraTouchArea, gestureValidator)
            )
        }
    }
}

class ChildrenTransitionScope<InteractionTarget : Any, NavState : Any>(
    private val containerSize: IntSize,
    private val appyxComponent: BaseAppyxComponent<InteractionTarget, NavState>,
    private val gestureExtraTouchArea: Dp,
    private val gestureValidator: GestureValidator
) {

    @Suppress("ComposableNaming", "LongMethod", "ModifierMissing")
    @Composable
    fun ParentNode<InteractionTarget>.children(
        block: @Composable (child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget>) -> Unit
    ) {

        val saveableStateHolder = rememberSaveableStateHolder()

        LaunchedEffect(this@ChildrenTransitionScope.appyxComponent) {
            this@ChildrenTransitionScope.appyxComponent
                .removedElements()
                .collect { deletedKeys ->
                    deletedKeys.forEach { navKey ->
                        saveableStateHolder.removeState(navKey)
                    }
                }
        }

        val density = LocalDensity.current
        val gestureExtraTouchAreaPx = with(density) { gestureExtraTouchArea.toPx() }
        val uiModels by this@ChildrenTransitionScope.appyxComponent.uiModels.collectAsState()
        val appyxComponent = this@ChildrenTransitionScope.appyxComponent

        uiModels
            .forEach { elementUiModel ->
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

                            Child(
                                elementUiModel = elementUiModel.copy(
                                    modifier = Modifier
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
                                                it
                                                    .boundsInParent()
                                                    .inflate(gestureExtraTouchAreaPx)
                                            offsetCenter = transformedBoundingBox.center - localCenter
                                        }
                                ),
                                saveableStateHolder = saveableStateHolder,
                                decorator = block
                            )
                        }
                    }
                }
            }
    }

    @Composable
    private fun elementOffset(
        elementSize: IntSize,
        containerSize: IntSize
    ): IntOffset {

        val positionInside = motionPropertyRenderValue<PositionInside.Value, PositionInside>()
        val positionOutside = motionPropertyRenderValue<PositionOutside.Value, PositionOutside>()
        val layoutDirection = LocalLayoutDirection.current

        val positionInsideOffset = positionInside?.let {
            it.alignment.align(elementSize, containerSize, layoutDirection)
        } ?: IntOffset.Zero

        val positionOutsideOffset = positionOutside?.let {
            it.alignment.align(elementSize, containerSize, layoutDirection)
        } ?: IntOffset.Zero

        return positionInsideOffset + positionOutsideOffset
    }
}
