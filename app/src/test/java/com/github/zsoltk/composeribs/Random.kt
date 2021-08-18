package com.github.zsoltk.composeribs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.lang.reflect.Modifier


enum class RoutingState {
    IN, SETTLED, STASH, RESTORE, OUT
}

@Composable
fun Random() {
    var currentState by remember { mutableStateOf(RoutingState.IN) }
    val transition = updateTransition(currentState)
}

//@Composable
//fun Wrapper(modifier: Modifier, child: @Composable () -> Unit) {
//}



@Composable
fun Child1View() {

}
