package com.bumble.appyx.interactions.core.ui.geometry

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Geometry1D<Output, Frame>(
    private val scope: CoroutineScope,
    initialValue: Float,
    private val onGeometryChange: (output: Output) -> Frame
) {
    private val animatableX = Animatable(initialValue)

    val value: Float
        get() = animatableX.value

    fun animateTo(
        segment: Output,
        targetValue: Float,
        animationSpec: AnimationSpec<Float>
    ): StateFlow<Frame> {
        val flow = MutableStateFlow(onGeometryChange(segment))

        scope.launch {
            val result = animatableX.animateTo(
                targetValue = targetValue,
                animationSpec = animationSpec,
            ) {
                val latest = onGeometryChange(segment)
                flow.update { latest }
            }
        }

        return flow
    }
}
