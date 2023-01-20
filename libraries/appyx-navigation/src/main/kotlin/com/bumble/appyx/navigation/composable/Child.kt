package com.bumble.appyx.navigation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode

@Composable
fun <NavTarget : Any, NavState : Any> ParentNode<NavTarget>.Child(
    frameModel: FrameModel<NavTarget, NavState>,
    saveableStateHolder: SaveableStateHolder,
    decorator: @Composable (child: ChildRenderer, frameModel: FrameModel<NavTarget, NavState>) -> Unit
) {
    val navKey = frameModel.navElement.key
    val childEntry = remember(navKey.id) { childOrCreate(navKey) }
    saveableStateHolder.SaveableStateProvider(key = navKey) {
        decorator(
            ChildRendererImpl(
                node = childEntry.node,
                frameModel = frameModel
            ),
            frameModel
        )
    }
}

private class ChildRendererImpl<NavTarget : Any, NavState : Any>(
    private val node: Node,
    private val frameModel: FrameModel<NavTarget, NavState>
) : ChildRenderer {

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke(modifier: Modifier) {
        Box(modifier = frameModel.modifier) {
            node.Compose(modifier = modifier)
        }
    }

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    override operator fun invoke() {
        Box(modifier = frameModel.modifier) {
            node.Compose(modifier = Modifier)
        }
    }
}
