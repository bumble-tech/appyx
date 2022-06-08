package com.bumble.appyx.routingsourcedemos.modal

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
import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState
import com.bumble.appyx.v2.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.v2.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.v2.core.routing.transition.TransitionSpec

@Suppress("TransitionPropertiesLabel")
class ModalTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<TransitionState, Float> = { tween(500) }
) : ModifierTransitionHandler<T, TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<TransitionState>,
        descriptor: TransitionDescriptor<T, TransitionState>
    ): Modifier = modifier.composed {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val offset = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    TransitionState.CREATED -> 1f
                    TransitionState.MODAL -> 0.5f
                    TransitionState.FULL_SCREEN -> 0f
                    TransitionState.DESTROYED -> -5f
                }
            })

        val height = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    TransitionState.CREATED -> 0f
                    TransitionState.MODAL -> 0.5f
                    TransitionState.FULL_SCREEN -> 1f
                    TransitionState.DESTROYED -> 1f
                }
            })

        val width = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    TransitionState.CREATED -> 0.8f
                    TransitionState.MODAL -> 0.9f
                    TransitionState.FULL_SCREEN -> 1f
                    TransitionState.DESTROYED -> 1f
                }
            })

        val cornerRadius = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    TransitionState.CREATED -> 20f
                    TransitionState.MODAL -> 20f
                    TransitionState.FULL_SCREEN -> 0f
                    TransitionState.DESTROYED -> 0f
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
