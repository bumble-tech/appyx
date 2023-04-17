package com.bumble.appyx.interactions

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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
import com.bumble.appyx.transitionmodel.testdrive.ui.md_amber_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_blue_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_blue_grey_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_cyan_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_grey_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_indigo_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_blue_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_green_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_lime_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_pink_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_teal_500

val manatee = Color(0xFF8D99AE)
val silver_sand = Color(0xFFBDC6D1)
val sizzling_red = Color(0xFFF05D5E)
val atomic_tangerine = Color(0xFFF0965D)

val colors = listOf(
    manatee,
    sizzling_red,
    atomic_tangerine,
    silver_sand,
    md_pink_500,
    md_indigo_500,
    md_blue_500,
    md_light_blue_500,
    md_cyan_500,
    md_teal_500,
    md_light_green_500,
    md_lime_500,
    md_amber_500,
    md_grey_500,
    md_blue_grey_500
)

@Composable
fun <InteractionTarget : Any, ModelState : Any> Children(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    ignoreVisibility: Boolean = false,
    element: @Composable (ElementUiModel<InteractionTarget>) -> Unit = {
        Element(elementUiModel = it)
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
            .composed {
                if (clipToBounds) {
                    clipToBounds()
                } else {
                    this
                }
            }
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
                if (ignoreVisibility || isVisible) {
                    element.invoke(frameModel)
                }
            }
        }
    }
}

@Composable
fun Element(
    color: Color? = Color.Unspecified,
    elementUiModel: ElementUiModel<*>,
    modifier: Modifier = Modifier.fillMaxSize(),
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
