package com.bumble.appyx.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.mapState
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import kotlinx.coroutines.flow.SharingStarted

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.PermanentChild(
    permanentNavModel: PermanentNavModel<NavTarget>,
    navTarget: NavTarget,
    decorator: @Composable (child: ChildRenderer) -> Unit
) {
    val scope = rememberCoroutineScope()
    val child by remember(navTarget, permanentNavModel) {
        permanentNavModel
            .elements
            // use WhileSubscribed or Lazy otherwise desynchronisation issue
            .mapState(scope, SharingStarted.WhileSubscribed()) { navElements ->
                navElements
                    .find { it.key.navTarget == navTarget }
                    ?.let { childOrCreate(it.key) }
            }
    }.collectAsState()

    child?.let {
        decorator(PermanentChildRender(it.node))
    }

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
