//package com.bumble.appyx.v2.core.routing.source.modal
//
//import androidx.compose.animation.core.Transition
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import com.bumble.appyx.v2.core.routing.transition.TransitionSpec
//import com.bumble.appyx.v2.core.routing.transition.UpdateTransitionHandler
//import com.bumble.appyx.v2.ui.atomic_tangerine
//import com.bumble.appyx.v2.ui.manatee
//import com.bumble.appyx.v2.ui.silver_sand
//import com.bumble.appyx.v2.ui.sizzling_red
//
//@Suppress("TransitionPropertiesLabel")
//class ModalTransitionHandler(
//    private val transitionSpec: TransitionSpec<Modal.TransitionState, Float> = { tween(500) }
//) : UpdateTransitionHandler<Modal.TransitionState>() {
//
//    @Composable
//    override fun map(transition: Transition<Modal.TransitionState>): Modifier {
//        val screenHeight = LocalConfiguration.current.screenHeightDp
//        val offset = transition.animateFloat(
//            transitionSpec = transitionSpec,
//            targetValueByState = {
//                when (it) {
//                    Modal.TransitionState.CREATED -> 1f
//                    Modal.TransitionState.MODAL -> 0.5f
//                    Modal.TransitionState.FULL_SCREEN -> 0f
//                    Modal.TransitionState.DESTROYED -> -5f
//                }
//            })
//
//        val height = transition.animateFloat(
//            transitionSpec = transitionSpec,
//            targetValueByState = {
//                when (it) {
//                    Modal.TransitionState.CREATED -> 0f
//                    Modal.TransitionState.MODAL -> 0.5f
//                    Modal.TransitionState.FULL_SCREEN -> 1f
//                    Modal.TransitionState.DESTROYED -> 1f
//                }
//            })
//
//        val width = transition.animateFloat(
//            transitionSpec = transitionSpec,
//            targetValueByState = {
//                when (it) {
//                    Modal.TransitionState.CREATED -> 0.8f
//                    Modal.TransitionState.MODAL -> 0.9f
//                    Modal.TransitionState.FULL_SCREEN -> 1f
//                    Modal.TransitionState.DESTROYED -> 1f
//                }
//            })
//
//        val cornerRadius = transition.animateFloat(
//            transitionSpec = transitionSpec,
//            targetValueByState = {
//                when (it) {
//                    Modal.TransitionState.CREATED -> 20f
//                    Modal.TransitionState.MODAL -> 20f
//                    Modal.TransitionState.FULL_SCREEN -> 0f
//                    Modal.TransitionState.DESTROYED -> 0f
//                }
//            })
//
////        val destroyProgress = transition.animateFloat(
////            transitionSpec = transitionSpec,
////            targetValueByState = {
////                when (it) {
////                    Modal.TransitionState.DESTROYED -> 1f
////                    else -> 0f
////                }
////            })
//
//        return Modifier
//            .offset(x = Dp(0f), y = Dp(screenHeight * offset.value))
//            .fillMaxWidth(width.value)
//            .fillMaxHeight(height.value)
//            .background(color = silver_sand, shape = RoundedCornerShape(Dp(cornerRadius.value)))
//    }
//}
