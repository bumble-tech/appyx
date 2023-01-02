package com.bumble.appyx.navmodel.spotlight.transitionhandler

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.toIntOffset

@Suppress("TransitionPropertiesLabel")
class SpotlightSlider<T : Parcelable>(
    private val transitionSpec: TransitionSpec<Spotlight.State, Offset> = {
        spring(stiffness = Spring.StiffnessVeryLow)
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, Spotlight.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Spotlight.State>,
        descriptor: TransitionDescriptor<T, Spotlight.State>
    ): Modifier = modifier.composed {
        val offset by transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    Spotlight.State.INACTIVE_BEFORE -> toOutsideLeft(width)
                    Spotlight.State.ACTIVE -> toCenter()
                    Spotlight.State.INACTIVE_AFTER -> toOutsideRight(width)
                }
            },
        )

        offset {
            offset.toIntOffset(density)
        }
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T : Parcelable> rememberSpotlightSlider(
    transitionSpec: TransitionSpec<Spotlight.State, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<T, Spotlight.State> = remember {
    SpotlightSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
