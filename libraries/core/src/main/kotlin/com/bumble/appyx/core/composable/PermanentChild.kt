package com.bumble.appyx.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.PermanentChild(
    permanentNavModel: PermanentNavModel<NavTarget>,
    navTarget: NavTarget,
    decorator: @Composable (child: ChildRenderer) -> Unit
) {
    val child = remember(navTarget, permanentNavModel) {
        permanentNavModel
            .elements
            .value
            .find { it.key.navTarget == navTarget }
            ?.let { childOrCreate(it.key) }
            ?: throw IllegalStateException(
                "No child found for $navTarget in $permanentNavModel. " +
                        "Add $navTarget to $permanentNavModel before calling PermanentChild."
            )
    }

    decorator(PermanentChildRender(child.node))
}

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.PermanentChild(
    permanentNavModel: PermanentNavModel<NavTarget>,
    navTarget: NavTarget,
) {
    PermanentChild(permanentNavModel, navTarget) { child -> child() }
}

private class PermanentChildRender(private val node: Node) : ChildRenderer {

    @Suppress(
        "ComposableNaming" // This wants to be 'Invoke' but that won't work with 'operator'.
    )
    @Composable
    override operator fun invoke(modifier: Modifier) {
        node.Compose(modifier)
    }

    @Suppress(
        "ComposableNaming" // This wants to be 'Invoke' but that won't work with 'operator'.
    )
    @Composable
    override operator fun invoke() {
        invoke(modifier = Modifier)
    }
}
