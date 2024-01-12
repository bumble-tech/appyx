package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.operation.add
import com.bumble.appyx.components.modal.operation.show
import com.bumble.appyx.components.modal.ui.ModalVisualisation
import com.bumble.appyx.interactions.core.AppyxInteractionsContainer
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.sample.android.Element
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.BaseVisualisation
import kotlin.math.roundToInt
import com.bumble.appyx.interactions.sample.InteractionTarget as Target

@ExperimentalMaterialApi
@Composable
@Suppress("LongMethod", "MagicNumber")
fun ModalExperiment(
    modifier: Modifier = Modifier,
    visualisation: (UiContext) -> BaseVisualisation<Target, ModalModel.State<Target>, *, *>
) {
    val items = listOf(Target.Child1)
    val modal = Modal(
        model = ModalModel(
            initialElements = items,
            savedStateMap = null
        ),
        visualisation = visualisation,
        gestureFactory = { ModalVisualisation.Gestures(it) },
        gestureSettleConfig = GestureSettleConfig(
            completionThreshold = 0.2f,
            completeGestureSpec = spring(),
            revertGestureSpec = spring(),
        ),
    )

    AppyxComponentSetup(modal)

    Column(
        modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        ModalUi(
            modal = modal,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .weight(0.9f)
        )

        Button(
            modifier = Modifier
                .weight(0.1f)
                .padding(horizontal = 42.dp, vertical = 12.dp)
                .fillMaxWidth(),
            onClick = {
                modal.add(Target.Child2)
                modal.show()
            }) { Text(text = "New modal") }
    }
}

@Composable
fun <InteractionTarget : Any> ModalUi(
    modal: Modal<InteractionTarget>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    AppyxInteractionsContainer(
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        clipToBounds = false,
        appyxComponent = modal,
        modifier = modifier,
        element = { elementUiModel ->
            Element(
                color = color,
                elementUiModel = elementUiModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}
