package com.bumble.appyx.navigation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.node.ParentNode

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.Child(
    elementUiModel: ElementUiModel<NavTarget>,
    decorator: @Composable (child: ChildRenderer, elementUiModel: ElementUiModel<NavTarget>) -> Unit
) {
    val navElement = elementUiModel.element
    val childEntry = remember(navElement.id) { childOrCreate(navElement) }
    decorator(
        ChildRendererImpl(
            node = childEntry.node,
            elementUiModel = elementUiModel
        ),
        elementUiModel
    )
}

private class ChildRendererImpl<NavTarget : Any>(
    private val node: AbstractNode,
    private val elementUiModel: ElementUiModel<NavTarget>
) : ChildRenderer {

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke(modifier: Modifier) {
        Box(modifier = elementUiModel.modifier) {
            node.Compose(modifier = modifier)
        }
    }

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke() {
        Box(modifier = elementUiModel.modifier) {
            node.Compose(modifier = Modifier)
        }
    }
}
