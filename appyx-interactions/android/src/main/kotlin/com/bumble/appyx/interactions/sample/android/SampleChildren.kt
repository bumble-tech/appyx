package com.bumble.appyx.interactions.sample.android

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.AppyxComponent
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
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
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    child: @Composable BoxScope.(Element<InteractionTarget>) -> Unit = { element ->
        SampleElement(colors = colors, element = element)
    },
) {
    AppyxComponent(
        appyxComponent = appyxComponent,
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        modifier = modifier,
        clipToBounds = clipToBounds,
        child = child
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
