package com.bumble.appyx.interactions.core.ui.geometry

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.State
import com.bumble.appyx.interactions.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Geometry1D<Output, Frame>(
    private val scope: CoroutineScope,
    initialValue: Float,
    private val onGeometryChange: (output: Output) -> Frame
) {
    val animatableX = Animatable(initialValue)

    val value: Float
        get() = animatableX.value

    fun asState(): State<Float> =
        animatableX.asState()

    fun snapTo(
        targetValue: Float,
    ) {
        scope.launch {
            animatableX.snapTo(
                targetValue = targetValue,
            )
        }
    }

    fun animateTo(
        targetValue: Float,
        animationSpec: AnimationSpec<Float>
    ) {
        scope.launch {
            val result = animatableX.animateTo(
                targetValue = targetValue,
                animationSpec = animationSpec,
            ) {
                Logger.log(this@Geometry1D.javaClass.simpleName, "Geometry update: $value")
            }
        }
    }
}
