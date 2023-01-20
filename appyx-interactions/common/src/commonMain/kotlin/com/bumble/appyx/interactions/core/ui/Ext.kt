import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.ui.GestureSpec

// TODO consider where to keep these

var DisableAnimations = false


@Composable
fun <NavTarget : Any, NavState : Any> Modifier.gestureModifier(
    interactionModel: InteractionModel<NavTarget, NavState>,
    key: Any,
    gestureSpec: GestureSpec = GestureSpec()
) = this.composed {

    val density = LocalDensity.current

    pointerInput(key) {
        detectDragGestures(
            onDragStart = { position -> interactionModel.onStartDrag(position) },
            onDrag = { change, dragAmount ->
                change.consume()
                interactionModel.onDrag(dragAmount, density)
            },
            onDragEnd = {
                interactionModel.onDragEnd(
                    completionThreshold = gestureSpec.completionThreshold,
                    completeGestureSpec = gestureSpec.completeGestureSpec,
                    revertGestureSpec = gestureSpec.revertGestureSpec
                )
            }
        )
    }
}

