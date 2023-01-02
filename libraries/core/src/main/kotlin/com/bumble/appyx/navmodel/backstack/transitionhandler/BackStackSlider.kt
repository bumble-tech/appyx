package com.bumble.appyx.navmodel.backstack.transitionhandler

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
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.BackStackOperation
import com.bumble.appyx.navmodel.backstack.operation.NewRoot
import com.bumble.appyx.navmodel.backstack.operation.Pop
import com.bumble.appyx.navmodel.backstack.operation.Push
import com.bumble.appyx.navmodel.backstack.operation.Remove
import com.bumble.appyx.navmodel.backstack.operation.Replace
import com.bumble.appyx.navmodel.backstack.operation.SingleTop.SingleTopReactivateBackStackOperation
import com.bumble.appyx.navmodel.backstack.operation.SingleTop.SingleTopReplaceBackStackOperation
import com.bumble.appyx.navmodel.toIntOffset

@Suppress("TransitionPropertiesLabel")
class BackStackSlider<T : Parcelable>(
    private val transitionSpec: TransitionSpec<BackStack.State, Offset> = {
        spring(stiffness = Spring.StiffnessVeryLow)
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val offset by transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.State.CREATED -> toOutsideRight(width)
                    BackStack.State.ACTIVE -> toCenter()
                    BackStack.State.STASHED -> toOutsideLeft(width)
                    BackStack.State.DESTROYED -> {
                        when (val operation = descriptor.operation as? BackStackOperation) {
                            is Push,
                            is Pop,
                            is Remove,
                            is SingleTopReactivateBackStackOperation -> toOutsideRight(width)
                            is Replace,
                            is NewRoot,
                            is SingleTopReplaceBackStackOperation -> toOutsideLeft(width)
                            null -> error("Unexpected operation: $operation")
                            else -> toOutsideRight(width)
                        }
                    }
                }
            })

        offset {
            offset.toIntOffset(density)
        }
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T : Parcelable> rememberBackstackSlider(
    transitionSpec: TransitionSpec<BackStack.State, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<T, BackStack.State> = remember {
    BackStackSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
