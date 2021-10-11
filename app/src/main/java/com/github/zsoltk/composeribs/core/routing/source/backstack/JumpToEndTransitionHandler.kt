//package com.github.zsoltk.composeribs.core.routing.source.backstack
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
//
//@Suppress("TransitionPropertiesLabel")
//class JumpToEndTransitionHandler<S> : TransitionHandler<S> {
//
//    @Composable
//    override fun handle(fromState: S, toState: S, onTransitionFinished: (S) -> Unit): Modifier {
//        onTransitionFinished(toState)
//
//        return Modifier
//    }
//}
