package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.modifiers.onPointerEvent
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel


@Composable
fun <InteractionTarget : Any, ModelState : Any> Children(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    childContent: @Composable (ElementUiModel<InteractionTarget>) -> Unit = {},
    childWrapper: @Composable (ElementUiModel<InteractionTarget>) -> Unit = { frameModel ->
        ChildWrapper(frameModel) {
            childContent(frameModel)
        }
    },
) {
    val density = LocalDensity.current
    val elementUiModels by appyxComponent.uiModels.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(coroutineScope) {
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
            elementUiModels
                .forEach { elementUiModel ->
                    key(elementUiModel.element.id) {
                        elementUiModel.persistentContainer()
                        val isVisible by elementUiModel.visibleState.collectAsState()
                        if (isVisible) {
                            CompositionLocalProvider(
                                LocalMotionProperties provides elementUiModel.motionProperties
                            ) {
                                childWrapper.invoke(elementUiModel)
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun ChildWrapper(
    elementUiModel: ElementUiModel<*>,
    modifier: Modifier = Modifier.fillMaxSize(),
    contentDescription: String? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .then(elementUiModel.modifier)
            .then(modifier)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            }
    ) {
        content()
    }
}

@Suppress(
    "MagicNumber",
    "UnstableCollections" // colors is not an immutable list
)
@Composable
fun SampleElement(
    elementUiModel: ElementUiModel<*>,
    colors: List<Color>,
    modifier: Modifier = Modifier.fillMaxSize(),
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
