package com.bumble.appyx.interactions.ui.property

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
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.SystemClock
import com.bumble.appyx.utils.multiplatform.AppyxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Keeps a value change in motion by calculating the rate of change of the wrapped Animatable value
 * even when snapping. This is used to seamlessly switch to animation with a natural initial velocity
 * rather than zero.
 *
 * @param coroutineScope
 * @param animatable The backing [Animatable] to animate internal values with.
 * @param easing The [Easing] function to apply when setting interpolated values.
 * See [easingTransform].
 * @param visibilityThreshold The visibility threshold that will be passed to [Animatable]
 * when animating.
 * @param displacement A flow of values to apply as a displacement on top of the internal values
 * that are backed by the internal [Animatable].
 */
abstract class MotionProperty<T, V : AnimationVector>(
    protected val coroutineScope: CoroutineScope,
    private val animatable: Animatable<T, V>,
    val easing: Easing? = null,
    private val visibilityThreshold: T? = null,
    private val displacement: StateFlow<T>
) {

    interface Target

    private var lastVelocity = animatable.velocity

    /**
     * Contains the previous value of [lastVelocity]. To be used in initial velocity instead of it,
     * as the values in the last animation frame can jump due to snapping if within threshold of target value,
     * and that would result in unrealistic speeds.
     */
    private var previousVelocity = animatable.velocity
    private var lastTime = 0L

    private val internalValueFlow = MutableStateFlow(animatable.value)


    /**
     * Contains the unmodified internal value of the [MotionProperty] backed by its [Animatable].
     * Useful if you need information on animation targets, i.e. what this [MotionProperty] thinks
     * it should display before that value being modified by any displacements.
     *
     * If you need render-ready values (for Modifiers, checking visibility, etc.) then you need
     * to use [renderValue] instead.
     */
    val internalValue: T
        get() = animatable.value

    /**
     * Contains the render-ready flow of values with displacements already applied.
     *
     * @see [renderValue]
     */
    val renderValueFlow: StateFlow<T> =
        displacement.combine(
            internalValueFlow
        ) { displacement, value ->
            calculateRenderValue(value, displacement)
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = internalValueFlow.value
        )

    /**
     * @return Should return the result of a subtraction [base - displacement] interpreted for <T>
     */
    abstract fun calculateRenderValue(base: T, displacement: T): T

    /**
     * Render-ready value that contains applied displacements on top of the [internalValue].
     */
    val renderValue: T
        get() = renderValueFlow.value

    abstract val modifier: Modifier

    private val _isAnimatingFlow = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean>
        get() = _isAnimatingFlow


    /**
     * Visibility of the element is calculated based on the element bounds relative to the container
     * where transition happen in case of clipToBounds = true or rootBounds in case of clipToBounds = true.
     * This property should be overridden if the value of MotionProperty affect visibility. For instance,
     * Alpha motion property - regardless of element bounds the element is invisible if alpha = 0f.
     */
    open val isVisibleFlow: StateFlow<Boolean>? = null

    private fun onValueChanged() {
        internalValueFlow.update { internalValue }
    }

    /**
     * Takes the supplied [Easing] as a priority to transform [fraction].
     * Falls back to using the default [Easing] if a priority wasn't specified.
     * Falls back to a simple [LinearEasing] if none of those were specified.
     */
    fun easingTransform(priority: Easing? = null, fraction: Float): Float {
        val resolved = priority ?: this.easing ?: LinearEasing
        return resolved.transform(fraction)
    }

    suspend fun snapTo(targetValue: T) {
        previousVelocity = lastVelocity
        lastVelocity = calculateVelocity(targetValue)
        animatable.snapTo(targetValue)
        onValueChanged()
    }

    @Suppress("MagicNumber")
    private fun calculateVelocity(targetValue: T): T {
        if (lastTime == 0L) lastTime = SystemClock.nanoTime()
        val converter = animatable.typeConverter

        val vec1: AnimationVector = converter.convertToVector(animatable.value)
        val targetVector = converter.convertToVector(targetValue)
        val time = SystemClock.nanoTime()
        val deltaTimeMs = (time - lastTime) / MillisToNanos
        val timeFactor = if (deltaTimeMs > 0) 1000 / deltaTimeMs else 0
        lastTime = time

        @Suppress("UNCHECKED_CAST")
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

        AppyxLogger.v("MotionProperty", "Calculated velocity: $velocity")
        return velocity
    }

    suspend fun animateTo(
        targetValue: T,
        animationSpec: AnimationSpec<T>,
        block: (Animatable<T, V>.() -> Unit) = {}
    ) {
        val animationSpec1 = insertVisibilityThreshold(animationSpec)
        AppyxLogger.d("MotionProperty", "Starting with initialVelocity = $previousVelocity")
        _isAnimatingFlow.update {
            targetValue != internalValue
        }
        val result = animatable.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec1,
            initialVelocity = previousVelocity
        ) {
            block(this)
            AppyxLogger.d(
                "MotionProperty",
                "Value = ${animatable.value}, Velocity = ${animatable.velocity})"
            )
            lastVelocity = animatable.velocity
            previousVelocity = animatable.velocity
            onValueChanged()
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

