package com.bumble.appyx.components.spotlight.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.ui.Visualisation
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.ComponentNode
import com.bumble.appyx.navigation.node.Node

/**
 * A simplified way of creating a Node with a pager/carousel-like navigation, allowing you to
 * quickly sketch out your navigation.
 *
 * In more complex scenarios you'll probably want to create your own Node class, but in many
 * cases this should offer a simple api to reduce the amount of code required.
 */
fun <T : Any> spotlightNode(
    nodeContext: NodeContext,
    items: List<T>,
    mappings: (Spotlight<T>, T, NodeContext) -> Node<*>,
    model: SpotlightModel<T> = SpotlightModel(
        items = items,
        savedStateMap = nodeContext.savedStateMap
    ),
    visualisation: (UiContext) -> Visualisation<T, State<T>> = { SpotlightSlider(it, model.currentState) },
    gestureFactory: (TransitionBounds) -> GestureFactory<T, State<T>> = {
        SpotlightSlider.Gestures(it)
    },
    content: @Composable (Spotlight<T>, Modifier) -> Unit = { spotlight, modifier ->
        AppyxNavigationContainer(
            appyxComponent = spotlight,
            modifier = modifier
        )
    }
) = ComponentNode(
    nodeContext = nodeContext,
    component = Spotlight(
        model = SpotlightModel(
            items = items,
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = visualisation,
        gestureFactory = gestureFactory
    ),
    mappings = mappings,
    content = content
)
