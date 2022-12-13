package com.bumble.appyx.app.node.backstack.app.custombackstack.transition

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStackOnScreenResolver.MAX_ON_SCREEN
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import kotlin.math.roundToInt

class RecentsTransitionHandler<NavTarget>(
    private val specFloat: TransitionSpec<State, Float> = { spring() },
    private val specDp: TransitionSpec<State, Dp> = { spring() },
    private val specOffset: TransitionSpec<State, Offset> = { spring() },
) : ModifierTransitionHandler<NavTarget, State>() {

    data class Props(
        val offset: Offset = Offset.Zero,
        val rotation: Float = 0f,
        val alpha: Float = 1f,
        val scale: Float = 1f,
        val zIndex: Float = Z_INDEX
    )

    private val created = Props()
    private val active = created
    private val stashed = active.copy(rotation = -3f)
    private val destroyed = created.copy(zIndex = 0f)
    private val activeItemIndex: State.Stashed.() -> Int = { size - 1 }

    private fun State.toProps(height: Float): Props =
        when (this) {
            is State.Created -> created.copy(offset = Offset(0f, 2f * height))
            is State.Active -> active.copy(offset = activeOffset())
            is State.Stashed -> stashed.copy(
                offset = stashedOffset(height),
                alpha = stashedAlpha(),
                scale = stashedScale(),
                zIndex = stashedZIndex()
            )
            is State.Destroyed -> destroyed.copy(offset = Offset(0f, 2f * height))
        }

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<State>,
        descriptor: TransitionDescriptor<NavTarget, State>
    ): Modifier = modifier.composed {
        val height = descriptor.params.bounds.height.value

        val offset by transition.animateOffset(
            transitionSpec = specOffset,
            targetValueByState = { it.toProps(height).offset },
            label = ""
        )

        val rotation by transition.animateFloat(
            transitionSpec = specFloat,
            targetValueByState = { it.toProps(height).rotation },
            label = ""
        )

        val scale by transition.animateFloat(
            transitionSpec = specFloat,
            targetValueByState = { it.toProps(height).scale },
            label = ""
        )

        val alpha by transition.animateFloat(
            transitionSpec = specFloat,
            targetValueByState = { it.toProps(height).alpha },
            label = ""
        )

        val zIndex by transition.animateFloat(
            transitionSpec = specFloat,
            targetValueByState = { it.toProps(height).zIndex },
            label = ""
        )

        this
            .offset {
                IntOffset(
                    x = (offset.x * density).roundToInt(),
                    y = (offset.y * density).roundToInt()
                )
            }
            .scale(scale)
            .graphicsLayer { rotationX = rotation }
            .drawBehind {
                drawRoundRect(
                    color = Color.Black,
                    cornerRadius = CornerRadius(x = 10f * density, y = 10f * density)
                )
            }
            .alpha(alpha)
            .zIndex(zIndex)
    }

    private fun activeOffset(): Offset = Offset.Zero

    private fun State.Stashed.stashedOffset(height: Float): Offset {
        val diff = height * SCALING_FACTOR
        val multiplier = activeItemIndex() - index

        return Offset(
            0f,
            -multiplier * (diff + ITEM_OFFSET)
        )
    }

    private fun State.Stashed.stashedScale(): Float {
        val activeScale = 1.0f
        val multiplier = activeItemIndex() - index

        return if (index < size - MAX_ON_SCREEN) {
            0f
        } else {
            activeScale - multiplier * SCALING_FACTOR
        }
    }

    private fun State.Stashed.stashedAlpha(): Float {
        val activeAlpha = 1.0f
        val multiplier = activeItemIndex() - index

        return if (index < size - MAX_ON_SCREEN) {
            0f
        } else {
            activeAlpha - multiplier * ALPHA_FACTOR
        }
    }

    private fun State.Stashed.stashedZIndex(): Float {
        val multiplier = activeItemIndex() - index

        return if (index < size - MAX_ON_SCREEN) {
            0f
        } else {
            Z_INDEX - multiplier
        }
    }

    companion object {
        const val Z_INDEX = MAX_ON_SCREEN.toFloat()
        const val ITEM_OFFSET = 20f
        const val SCALING_FACTOR = 0.1f
        const val ALPHA_FACTOR = 0.3f
    }
}

@Composable
fun <R> rememberRecentsTransitionHandler(
    specFloat: TransitionSpec<State, Float> = { spring(stiffness = Spring.StiffnessMediumLow) },
    specDp: TransitionSpec<State, Dp> = { spring(stiffness = Spring.StiffnessMediumLow) },
    specOffset: TransitionSpec<State, Offset> = { spring(stiffness = Spring.StiffnessMediumLow) },
): ModifierTransitionHandler<R, State> =
    remember {
        RecentsTransitionHandler(specFloat, specDp, specOffset)
    }
