package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.runtime.State
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.property.Property

abstract class AnimatedProperty<T, V : AnimationVector>(
    protected val animatable: Animatable<T, V>
) : Property<T> {

    /**
     * When in interpolation mode, the Animatable is snapping and doesn't have a concept of velocity.
     * However, we can calculate it as a difference between the latest two snapped values,
     * then use it as an initial velocity when switching to animation, so that the animation
     * starts from a natural speed rather than zero.
     */
    private var lastVelocity = animatable.velocity
    private var lastTime = 0L


    override val value: T
        get() = animatable.value

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

    override suspend fun animateTo(targetValue: T, animationSpec: AnimationSpec<T>) {
        Logger.log("Animatable", "Starting with initialVelocity = $lastVelocity")
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec,
            initialVelocity = lastVelocity
        ) {
            Logger.log("Animatable", "Value = ${animatable.value}, Velocity = ${animatable.velocity})")
            lastVelocity = animatable.velocity
        }
    }

    companion object {
        internal const val MillisToNanos: Long = 1_000_000L
    }
}
