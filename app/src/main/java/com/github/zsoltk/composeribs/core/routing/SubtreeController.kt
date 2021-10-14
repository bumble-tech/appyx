package com.github.zsoltk.composeribs.core.routing

// TODO possibly remove this completely

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler

class SubtreeController<T, S>(
    val routingSource: RoutingSource<T, S>,
//    private val transitionHandler: TransitionHandler<S>? = null
) {

//    @Composable
//    fun getModifierSnapshot(key: RoutingKey<T>): Modifier {
//        val element =
//            routingSource.elements.firstOrNull { it.key == key }
//                ?: routingSource.pendingRemoval.firstOrNull { it.key == key }
//                ?:
//                // TODO proper error handling
//                //  consider that routing source not containing might be both framework and client code error
//                //  depending where the routing source is implemented at
//                return Modifier
//
////        if (transitionHandler == null) {
//            LaunchedEffect(key) {
//                routingSource.onTransitionFinished(key)
//            }
//            return Modifier
////        }
//
////        return transitionHandler.handle(
////            fromState = element.fromState,
////            toState = element.targetState,
////            onTransitionFinished = {
////                routingSource.onTransitionFinished(key)
////            }
////        )
//    }
}
