package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.property.Property

abstract class AnimatedProperty<T, V : AnimationVector>(
    protected val animatable: Animatable<T, V>
) : Property<T> {

    // TODO
    //   This is a workaround. Velocity shouldn't be reset to 0 across interrupts, and yet it is.
    //   This helps to carry it over across different calls of [animateTo].
    //   Figure out why, and then this can be removed.
    private var lastVelocity = animatable.velocity

    override val value: T
        get() = animatable.value

    override suspend fun snapTo(targetValue: T) {
        animatable.snapTo(targetValue)
    }

    override suspend fun animateTo(targetValue: T, animationSpec: AnimationSpec<T>) {
        Logger.log("Animatable", "Velocity = ${animatable.velocity} (${animatable.hashCode()}")
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec,
            initialVelocity = lastVelocity
        ) {
            Logger.log("Animatable", "Value = ${animatable.value}, Velocity = ${animatable.velocity} (${animatable.hashCode()}")
            lastVelocity = animatable.velocity
        }
    }
}
