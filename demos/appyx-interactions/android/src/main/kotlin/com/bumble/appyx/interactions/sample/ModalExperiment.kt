package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.ui.ModalMotionController
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.android.Children
import com.bumble.appyx.interactions.sample.android.Element
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.interactions.sample.InteractionTarget as Target

@ExperimentalMaterialApi
@Composable
@Suppress("LongMethod", "MagicNumber")
fun ModalExperiment(
    modifier: Modifier = Modifier,
    motionController: (UiContext) -> BaseMotionController<Target, ModalModel.State<Target>, *, *>
) {
    val items = listOf(
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
    )
    val modal = Modal(
        model = ModalModel(
            initialElements = items,
            savedStateMap = null
        ),
        motionController = motionController,
        gestureFactory = { ModalMotionController.ModalGestures(it) },
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 4),
        gestureSettleConfig = GestureSettleConfig(
            completionThreshold = 0.2f,
            completeGestureSpec = spring(),
            revertGestureSpec = spring(),
        ),
    )

    InteractionModelSetup(modal)

        ModalUi(
            modal = modal,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )
}

@Composable
fun <InteractionTarget : Any> ModalUi(
    modal: Modal<InteractionTarget>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Children(
        clipToBounds = false,
        interactionModel = modal,
        modifier = modifier,
        element = { elementUiModel ->
            Element(
                color = color,
                elementUiModel = elementUiModel,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(elementUiModel.element.id) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                modal.onDrag(dragAmount, this)
                            },
                            onDragEnd = {
                                AppyxLogger.d("drag", "end")
                                modal.onDragEnd()
                            }
                        )
                    }
            )
        }
    )
}
