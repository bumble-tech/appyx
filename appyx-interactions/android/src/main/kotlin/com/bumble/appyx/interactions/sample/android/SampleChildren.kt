package com.bumble.appyx.interactions.sample.android

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.composable.AppyxInteractionsContainer
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.sample.SampleElement
import com.bumble.appyx.interactions.sample.colors
import kotlin.math.roundToInt

/**
 * Renders children with the [SampleElement] composable.
 *
 * For real-life use-cases use the [Children] wrapper directly.
 */
@Composable
fun <InteractionTarget : Any, ModelState : Any> SampleAppyxContainer(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    elementUi: @Composable BoxScope.(Element<InteractionTarget>) -> Unit = { element ->
        SampleElement(colors = colors, element = element)
    },
) {
    AppyxInteractionsContainer(
        appyxComponent = appyxComponent,
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        modifier = modifier,
        clipToBounds = clipToBounds,
        elementUi = elementUi
    )
}

@Composable
fun Element(
    element: Element<*>,
    modifier: Modifier = Modifier,
    color: Color? = Color.Unspecified,
    contentDescription: String? = null
) {
    SampleElement(
        element = element,
        modifier = modifier,
        colors = colors,
        color = color,
        contentDescription = contentDescription,
    )
}
