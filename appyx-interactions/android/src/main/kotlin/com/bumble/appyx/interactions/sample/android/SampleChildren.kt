package com.bumble.appyx.interactions.sample.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.sample.Children
import com.bumble.appyx.interactions.sample.SampleElement
import com.bumble.appyx.interactions.sample.colors
import kotlin.math.roundToInt

/**
 * Renders children with the [SampleElement] composable.
 *
 * For real-life use-cases use the [Children] wrapper directly.
 */
@Composable
fun <InteractionTarget : Any, ModelState : Any> SampleChildren(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    element: @Composable (ElementUiModel<InteractionTarget>) -> Unit = {
        SampleElement(colors = colors, elementUiModel = it)
    },
) {
    Children(
        interactionModel = interactionModel,
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        modifier = modifier,
        clipToBounds = clipToBounds,
        childWrapper = { elementUiModel, content ->
            element(elementUiModel)
        },
    )
}

@Composable
fun Element(
    elementUiModel: ElementUiModel<*>,
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color? = Color.Unspecified,
    contentDescription: String? = null
) {
    SampleElement(
        elementUiModel = elementUiModel,
        modifier = modifier,
        colors = colors,
        color = color,
        contentDescription = contentDescription,
    )
}
