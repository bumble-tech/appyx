package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.model.AppyxComponent
import com.bumble.appyx.navigation.modality.NodeContext

/**
 * A pre-wired node to build component-specific helper methods on.
 */
open class ComponentNode<T : Any, C : AppyxComponent<T, *>>(
    nodeContext: NodeContext,
    private val component: C,
    private val mappings: (C, T, NodeContext) -> Node<*>,
    private val content: @Composable (C, Modifier) -> Unit
) : Node<T>(
    appyxComponent = component,
    nodeContext = nodeContext
) {
    override fun buildChildNode(navTarget: T, nodeContext: NodeContext): Node<*> =
        mappings(component, navTarget, nodeContext)

    @Composable
    override fun Content(modifier: Modifier) {
        content(component, modifier)
    }
}
