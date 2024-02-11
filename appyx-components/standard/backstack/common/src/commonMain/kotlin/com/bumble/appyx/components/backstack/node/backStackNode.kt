package com.bumble.appyx.components.backstack.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.ComponentNode
import com.bumble.appyx.navigation.node.Node

/**
 * A simplified way of creating a Node with back stack navigation, allowing you to
 * quickly sketch out your navigation.
 *
 * In more complex scenarios you'll probably want to create your own Node class, but in many
 * cases this should offer a simple api to reduce the amount of code required.
 */
fun <T : Any> backStackNode(
    nodeContext: NodeContext,
    initialTarget: T,
    mappings: (BackStack<T>, T, NodeContext) -> Node<*>,
    visualisation: (UiContext) -> Visualisation<T, State<T>> = { BackStackFader(it) },
    gestureFactory: (TransitionBounds) -> GestureFactory<T, State<T>> = {
        GestureFactory.Noop()
    },
    content: @Composable (BackStack<T>, Modifier) -> Unit = { backStack, modifier ->
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = modifier
        )
    }
) = ComponentNode(
    nodeContext = nodeContext,
    component = BackStack(
        model = BackStackModel(
            initialTarget = initialTarget,
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = visualisation,
        gestureFactory = gestureFactory
    ),
    mappings = mappings,
    content = content
)
