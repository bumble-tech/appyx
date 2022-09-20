package com.bumble.appyx.navmodel.modal

import android.annotation.SuppressLint
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
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
class ModalTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<State, Float> = { tween(500) }
) : ModifierTransitionHandler<T, State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<State>,
        descriptor: TransitionDescriptor<T, State>
    ): Modifier = modifier.composed {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val offset = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 1f
                    State.MODAL -> 0.5f
                    State.FULL_SCREEN -> 0f
                    State.DESTROYED -> -5f
                }
            })

        val height = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 0f
                    State.MODAL -> 0.5f
                    State.FULL_SCREEN -> 1f
                    State.DESTROYED -> 1f
                }
            })

        val width = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 0.8f
                    State.MODAL -> 0.9f
                    State.FULL_SCREEN -> 1f
                    State.DESTROYED -> 1f
                }
            })

        val cornerRadius = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    State.CREATED -> 20f
                    State.MODAL -> 20f
                    State.FULL_SCREEN -> 0f
                    State.DESTROYED -> 0f
                }
            })

        offset(x = Dp(0f), y = Dp(screenHeight * offset.value))
            .fillMaxWidth(width.value)
            .fillMaxHeight(height.value)
            .background(
                color = Color(0xFFBDC6D1),
                shape = RoundedCornerShape(Dp(cornerRadius.value))
            )
    }

}
