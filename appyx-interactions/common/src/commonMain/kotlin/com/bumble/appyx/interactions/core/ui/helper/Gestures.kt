package com.bumble.appyx.interactions.core.ui.helper

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent

// TODO consider where to keep these

var DisableAnimations = false

val DefaultAnimationSpec: SpringSpec<Float> = spring()

@Composable
@Suppress("UnnecessaryComposedModifier", "ModifierComposable")
fun <InteractionTarget : Any, ModelState : Any> Modifier.gestureModifier(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    key: Any,
) = this.composed {

    val density = LocalDensity.current

    pointerInput(key) {
        detectDragGestures(
            onDragStart = { position -> appyxComponent.onStartDrag(position) },
            onDrag = { change, dragAmount ->
                change.consume()
                appyxComponent.onDrag(dragAmount, density)
            },
            onDragEnd = { appyxComponent.onDragEnd() }
        )
    }
}

