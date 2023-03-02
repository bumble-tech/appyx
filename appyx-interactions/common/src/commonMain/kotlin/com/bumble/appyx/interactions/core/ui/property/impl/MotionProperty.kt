package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.property.Property
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Keeps a value change in motion by calculating the rate of change of the wrapped Animatable value
 * even when snapping. This is used to seamlessly switch to animation with a natural initial velocity
 * rather than zero.
 */
abstract class MotionProperty<T, V : AnimationVector>(
    protected val animatable: Animatable<T, V>,
    protected val easing: Easing? = null,
    private val visibilityThreshold: T? = null,
) : Property<T, V> {
    private var lastVelocity = animatable.velocity
    private var lastTime = 0L

    override val value: T
        get() = animatable.value

    private val _isAnimatingFlow = MutableStateFlow(false)
    override val isAnimating: Flow<Boolean>
        get() = _isAnimatingFlow

    /**
     * Takes the supplied [Easing] as a priority to transform [fraction].
     * Falls back to using the default [Easing] if a priority wasn't specified.
     * Falls back to a simple [LinearEasing] if none of those were specified.
     */
    fun easingTransform(priority: Easing? = null, fraction: Float): Float {
        val resolved = priority ?: this.easing ?: LinearEasing
        return resolved.transform(fraction)
    }

    override suspend fun snapTo(targetValue: T) {
        lastVelocity = calculateVelocity(targetValue)
        animatable.snapTo(targetValue)
    }

    private fun calculateVelocity(targetValue: T): T {
        if (lastTime == 0L) lastTime = System.nanoTime()
        val converter = animatable.typeConverter

        val vec1: AnimationVector = converter.convertToVector(animatable.value)
        val targetVector = converter.convertToVector(targetValue)
        val time = System.nanoTime()
        val deltaTimeMs = (time - lastTime) / MillisToNanos
        val timeFactor = if (deltaTimeMs > 0) 1000 / deltaTimeMs else 0
        lastTime = time

        val velocity = when (vec1) {
            is AnimationVector1D -> {
                val vec2 = targetVector as AnimationVector1D
                converter.convertFromVector(
                    AnimationVector1D(
                        timeFactor * (vec2.value - vec1.value)
                    ) as V
                )
            }

            is AnimationVector2D -> {
                val vec2 = targetVector as AnimationVector2D
                converter.convertFromVector(
                    AnimationVector2D(
                        v1 = timeFactor * (vec2.v1 - vec1.v1),
                        v2 = timeFactor * (vec2.v2 - vec1.v2),
                    ) as V
                )
            }

            is AnimationVector3D -> {
                val vec2 = targetVector as AnimationVector3D
                converter.convertFromVector(
                    AnimationVector3D(
                        v1 = timeFactor * (vec2.v1 - vec1.v1),
                        v2 = timeFactor * (vec2.v2 - vec1.v2),
                        v3 = timeFactor * (vec2.v3 - vec1.v3),
                    ) as V
                )
            }

            is AnimationVector4D -> {
                val vec2 = targetVector as AnimationVector4D
                converter.convertFromVector(
                    AnimationVector4D(
                        v1 = timeFactor * (vec2.v1 - vec1.v1),
                        v2 = timeFactor * (vec2.v2 - vec1.v2),
                        v3 = timeFactor * (vec2.v3 - vec1.v3),
                        v4 = timeFactor * (vec2.v4 - vec1.v4),
                    ) as V
                )
            }
        }

        Logger.v("Animatable", "Calculated velocity: $velocity")
        return velocity
    }

    override suspend fun animateTo(
        targetValue: T,
        animationSpec: AnimationSpec<T>,
        block: (Animatable<T, V>.() -> Unit)
    ) {
        val animationSpec1 = insertVisibilityThreshold(animationSpec)
        Logger.log("Animatable", "Starting with initialVelocity = $lastVelocity")
        _isAnimatingFlow.update {
            targetValue != value
        }
        val result = animatable.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec1,
            initialVelocity = lastVelocity
        ) {
            block(this)
            Logger.log(
                "Animatable",
                "Value = ${animatable.value}, Velocity = ${animatable.velocity})"
            )
            lastVelocity = animatable.velocity
        }
        _isAnimatingFlow.update {
            result.endState.value != targetValue
        }
    }

    private fun insertVisibilityThreshold(animationSpec: AnimationSpec<T>) =
        if (animationSpec is SpringSpec<T>) {
            spring(
                stiffness = animationSpec.stiffness,
                dampingRatio = animationSpec.dampingRatio,
                visibilityThreshold = visibilityThreshold
            )
        } else animationSpec

    companion object {
        internal const val MillisToNanos: Long = 1_000_000L
    }
}
