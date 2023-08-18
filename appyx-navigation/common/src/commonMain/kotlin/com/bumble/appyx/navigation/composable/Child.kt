package com.bumble.appyx.navigation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode

@Composable
fun <InteractionTarget : Any> ParentNode<InteractionTarget>.Child(
    elementUiModel: ElementUiModel<InteractionTarget>,
    saveableStateHolder: SaveableStateHolder,
    decorator: @Composable (child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget>) -> Unit
) {
    val navElement = elementUiModel.element
    val childEntry = remember(navElement.id) { childOrCreate(navElement) }
    saveableStateHolder.SaveableStateProvider(key = navElement) {
        decorator(
            ChildRendererImpl(
                node = childEntry.node,
                elementUiModel = elementUiModel
            ),
            elementUiModel
        )
    }
}

private class ChildRendererImpl<InteractionTarget : Any>(
    private val node: Node,
    private val elementUiModel: ElementUiModel<InteractionTarget>
) : ChildRenderer {

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke(modifier: Modifier) {
        Box(modifier = elementUiModel.modifier) {
            CompositionLocalProvider(
                LocalMotionProperties provides elementUiModel.motionProperties
            ) {
                node.Compose(modifier = modifier)
            }
        }
    }

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke() {
        Box(modifier = elementUiModel.modifier) {
            CompositionLocalProvider(
                LocalMotionProperties provides elementUiModel.motionProperties
            ) {
                node.Compose(modifier = Modifier)
            }
        }
    }
}
