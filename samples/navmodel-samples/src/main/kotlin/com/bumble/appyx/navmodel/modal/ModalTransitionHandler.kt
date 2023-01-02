package com.bumble.appyx.navmodel.modal

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.modal.Modal.State

@Suppress("TransitionPropertiesLabel")
class ModalTransitionHandler<T : Parcelable>(
    private val transitionSpec: TransitionSpec<State, Float> = { tween(500) }
) : ModifierTransitionHandler<T, State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<State>,
        descriptor: TransitionDescriptor<T, State>
    ): Modifier = modifier.composed {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val offset = getOffset(transition)
        val height = getHeight(transition)
        val width = getWidth(transition)
        val cornerRadius = getCornerRadius(transition)

        offset(x = Dp(0f), y = Dp(screenHeight * offset.value))
            .fillMaxWidth(width.value)
            .fillMaxHeight(height.value)
            .background(
                color = Color(0xFFBDC6D1),
                shape = RoundedCornerShape(Dp(cornerRadius.value))
            )
    }

    @Composable
    private fun getOffset(transition: Transition<State>) =
        transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 1f
                    State.MODAL -> 0.5f
                    State.FULL_SCREEN -> 0f
                    State.DESTROYED -> -5f
                }
            })

    @Composable
    private fun getHeight(transition: Transition<State>) =
        transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 0f
                    State.MODAL -> 0.5f
                    State.FULL_SCREEN -> 1f
                    State.DESTROYED -> 1f
                }
            })

    @Composable
    private fun getWidth(transition: Transition<State>) =
        transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 0.8f
                    State.MODAL -> 0.9f
                    State.FULL_SCREEN -> 1f
                    State.DESTROYED -> 1f
                }
            })

    @Composable
    private fun getCornerRadius(transition: Transition<State>) =
        transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 20f
                    State.MODAL -> 20f
                    State.FULL_SCREEN -> 0f
                    State.DESTROYED -> 0f
                }
            })

}
