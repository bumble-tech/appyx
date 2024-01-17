package com.bumble.appyx.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.Child(
    element: Element<NavTarget>,
    decorator: @Composable (child: ChildRenderer, element: Element<NavTarget>) -> Unit
) {
    val childEntry = remember(element.id) { childOrCreate(element) }
    decorator(
        ChildRendererImpl(
            node = childEntry.node
        ),
        element
    )
}

private class ChildRendererImpl(
    private val node: Node
) : ChildRenderer {

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke(modifier: Modifier) {
        node.Compose(modifier = modifier)
    }

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke() {
        node.Compose(modifier = Modifier)
    }
}
