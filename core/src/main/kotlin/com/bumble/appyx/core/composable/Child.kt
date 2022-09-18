package com.bumble.appyx.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionBounds
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionParams
import kotlinx.coroutines.flow.map

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    navElement: NavElement<Routing, out State>,
    saveableStateHolder: SaveableStateHolder,
    transitionParams: TransitionParams,
    transitionHandler: TransitionHandler<Routing, State>,
    decorator: @Composable ChildTransitionScope<State>.(
        child: ChildRenderer,
        transitionDescriptor: TransitionDescriptor<Routing, State>
    ) -> Unit
) {
    val childEntry = remember(navElement.key.id) { childOrCreate(navElement.key) }
    saveableStateHolder.SaveableStateProvider(key = navElement.key) {
        val descriptor = remember(navElement) {
            navElement.createDescriptor(transitionParams)
        }
        val transitionScope = transitionHandler.handle(
            descriptor = descriptor,
            onTransitionFinished = {
                navModel.onTransitionFinished(childEntry.key)
            }
        )

        transitionScope.decorator(
            child = ChildRendererImpl(
                node = childEntry.node,
                transitionModifier = transitionScope.transitionModifier
            ),
            transitionDescriptor = descriptor,
        )
    }
}

private class ChildRendererImpl(
    private val node: Node,
    private val transitionModifier: Modifier
) : ChildRenderer {

    @Composable
    override operator fun invoke(modifier: Modifier) {
        Box(modifier = transitionModifier) {
            node.Compose(modifier = modifier)
        }
    }

    @Composable
    override operator fun invoke() {
        Box(modifier = transitionModifier) {
            node.Compose(modifier = Modifier)
        }
    }
}

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    navElement: NavElement<Routing, out State>,
    transitionHandler: TransitionHandler<Routing, State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(
        child: ChildRenderer,
        transitionDescriptor: TransitionDescriptor<Routing, State>
    ) -> Unit = { child, _ -> child() }
) {
    val density = LocalDensity.current.density
    var transitionBounds by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by remember(transitionBounds) {
        derivedStateOf {
            TransitionParams(
                bounds = TransitionBounds(
                    width = Dp(transitionBounds.width / density),
                    height = Dp(transitionBounds.height / density)
                )
            )
        }
    }
    Box(modifier = Modifier
        .onSizeChanged {
            transitionBounds = it
        }
    ) {
        key(navElement.key.id) {
            Child(
                navElement = navElement,
                saveableStateHolder = rememberSaveableStateHolder(),
                transitionParams = transitionParams,
                transitionHandler = transitionHandler,
                decorator = decorator
            )
        }
    }
}

private fun <Routing : Any, State> NavElement<Routing, State>.createDescriptor(
    transitionParams: TransitionParams
) =
    TransitionDescriptor(
        params = transitionParams,
        operation = operation,
        element = key.navTarget,
        fromState = fromState,
        toState = targetState
    )

@Composable
fun <R, S> NavModel<R, S>?.childrenAsState(): State<NavElements<R, out S>> =
    if (this != null) {
        elements.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }

@Composable
fun <R, S> NavModel<R, S>?.visibleChildrenAsState(): State<NavElements<R, out S>> =
    if (this != null) {
        val visibleElementsFlow = remember { screenState.map { it.onScreen } }
        visibleElementsFlow.collectAsState(emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

@Immutable
interface ChildTransitionScope<S> {
    val transition: Transition<S>
    val transitionModifier: Modifier
}

@Immutable
internal class ChildTransitionScopeImpl<S>(
    override val transition: Transition<S>,
    override val transitionModifier: Modifier
) : ChildTransitionScope<S>
