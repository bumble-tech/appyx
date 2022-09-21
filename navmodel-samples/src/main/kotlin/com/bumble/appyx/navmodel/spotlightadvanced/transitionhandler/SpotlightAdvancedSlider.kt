package com.bumble.appyx.navmodel.spotlightadvanced.transitionhandler

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveBefore
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class SpotlightAdvancedSlider<T>(
    private val transitionSpec: TransitionSpec<SpotlightAdvanced.State, Offset> = {
        spring(stiffness = Spring.StiffnessLow)
    }
) : ModifierTransitionHandler<T, SpotlightAdvanced.State>() {

    @Suppress("ModifierFactoryExtensionFunction", "MagicNumber")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<SpotlightAdvanced.State>,
        descriptor: TransitionDescriptor<T, SpotlightAdvanced.State>
    ): Modifier = modifier.composed {
        val offset by transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    is InactiveBefore -> toOutsideLeft(width)
                    is Active -> toCenter()
                    is InactiveAfter -> toOutsideRight(width)
                    is Carousel -> toCarousel(transition, descriptor).value
                }
            },
        )

        val scale by transition.animateFloat(
            transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
            targetValueByState = {
                when (it) {
                    is Active -> 1f
                    is InactiveAfter -> 1f
                    is InactiveBefore -> 1f
                    is Carousel -> 0.25f
                }
            }
        )

        val cornerPercent by transition.animateInt(
            transitionSpec = { spring() },
            targetValueByState = {
                when (it) {
                    is Active -> 0
                    is InactiveAfter -> 0
                    is InactiveBefore -> 0
                    is Carousel -> 20
                }
            }
        )

        offset {
            IntOffset(
                x = (offset.x * density).roundToInt(),
                y = (offset.y * density).roundToInt()
            )
        }
            .then(
                if (descriptor.toState is Carousel) {
                    aspectRatio(1f)
                } else Modifier
            )
            .scale(scale)
            .clip(RoundedCornerShape(cornerPercent))
    }

    @Composable
    @Suppress("MagicNumber")
    private fun toCarousel(
        transition: Transition<SpotlightAdvanced.State>,
        descriptor: TransitionDescriptor<T, SpotlightAdvanced.State>
    ): androidx.compose.runtime.State<Offset> {
        val radius = descriptor.params.bounds.width.value / 3
        val halfWidthDp =
            (descriptor.params.bounds.width.value - radius) / 2
        val halfHeightDp =
            (descriptor.params.bounds.height.value - radius) / 2
        val radiusDp = min(halfWidthDp, halfHeightDp) * 1f
        val angleDegrees = transition.animateFloat(
            transitionSpec = { spring() },
            targetValueByState = {
                when (it) {
                    is Active -> 0f
                    is InactiveAfter -> 0f
                    is InactiveBefore -> 0f
                    is Carousel -> (it.offset * 1.0f / it.max) * 360
                }
            }
        )
        return remember {
            derivedStateOf {
                val angleRadians =
                    Math.toRadians(angleDegrees.value.toDouble() % 360 - 90)
                val x = radiusDp * cos(angleRadians)
                val y =
                    radiusDp * sin(angleRadians) + descriptor.params.bounds.height.value / 6
                Offset(x.toFloat(), y.toFloat())
            }
        }
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T> rememberSpotlightAdvancedSlider(
    transitionSpec: TransitionSpec<SpotlightAdvanced.State, Offset> = { spring(stiffness = Spring.StiffnessLow) }
): ModifierTransitionHandler<T, SpotlightAdvanced.State> = remember {
    SpotlightAdvancedSlider(
        transitionSpec = transitionSpec
    )
}
