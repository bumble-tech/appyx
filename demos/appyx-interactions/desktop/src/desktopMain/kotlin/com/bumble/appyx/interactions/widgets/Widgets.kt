@file:Suppress("MatchingDeclarationName")
package com.bumble.appyx.interactions.widgets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.demos.common.widget.CalendarWidget
import com.bumble.appyx.demos.common.widget.TimerWidget
import com.bumble.appyx.demos.common.widget.WeatherWidget
import com.bumble.appyx.interactions.Events
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.composable.AppyxInteractionsContainer
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.widgets.ui.WidgetsStack3D
import kotlinx.coroutines.flow.Flow

enum class WidgetsType {
    Weather, Timer, Calendar,
}

@Composable
fun Widgets(
    events: Flow<Events>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        WidgetsType.Weather,
        WidgetsType.Timer,
        WidgetsType.Calendar,
    )
    val spotlight = remember {
        Spotlight(
            model = SpotlightModel(
                items = items,
                savedStateMap = null
            ),
            visualisation = { WidgetsStack3D(it) },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 4),
            gestureFactory = {
                SpotlightSlider.Gestures(
                    transitionBounds = it,
                    orientation = Orientation.Vertical,
                    reverseOrientation = true,
                )
            },
            gestureSettleConfig = GestureSettleConfig(
                completionThreshold = 0.2f,
                completeGestureSpec = spring(),
                revertGestureSpec = spring(),
            ),
        )
    }

    LaunchedEffect(events) {
        events.collect {
            when (it) {
                Events.OnUpClicked -> spotlight.previous()
                Events.OnDownClicked -> spotlight.next()
            }
        }
    }

    AppyxComponentSetup(spotlight)

    WidgetsUi(
        modifier = modifier,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        spotlight = spotlight,
    )
}

@Composable
private fun WidgetsUi(
    spotlight: Spotlight<WidgetsType>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    AppyxInteractionsContainer(
        clipToBounds = false,
        appyxComponent = spotlight,
        modifier = modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        elementUi = {
            WidgetTypeElement(
                element = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(240.dp)
            )
        },
    )
}

@Composable
private fun WidgetTypeElement(
    element: Element<WidgetsType>,
    modifier: Modifier = Modifier,
) {
    when (element.interactionTarget) {
        WidgetsType.Weather ->
            WeatherWidget(
                currentTemperature = 22.3f,
                lowTemperature = 16.7f,
                highTemperature = 31.4f,
                painterResource("sky.png"),
                modifier = modifier
            )

        WidgetsType.Timer ->
            TimerWidget(
                modifier = modifier
            )

        WidgetsType.Calendar ->
            CalendarWidget(
                modifier = modifier
            )
    }
}
