import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.model.BaseInteractionModel

// TODO consider where to keep these

var DisableAnimations = false

val DefaultAnimationSpec: SpringSpec<Float> = spring()

@Composable
@Suppress("UnnecessaryComposedModifier")
fun <InteractionTarget : Any, ModelState : Any> Modifier.gestureModifier(
    interactionModel: BaseInteractionModel<InteractionTarget, ModelState>,
    key: Any,
) = this.composed {

    val density = LocalDensity.current

    pointerInput(key) {
        detectDragGestures(
            onDragStart = { position -> interactionModel.onStartDrag(position) },
            onDrag = { change, dragAmount ->
                change.consume()
                interactionModel.onDrag(dragAmount, density)
            },
            onDragEnd = { interactionModel.onDragEnd() }
        )
    }
}

