package com.bumble.appyx.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.permanent.PermanentAppyxComponent
import com.bumble.appyx.navigation.mapState
import com.bumble.appyx.navigation.node.ParentNode
import kotlinx.coroutines.flow.SharingStarted

@Composable
fun <NavTarget : Any> ParentNode<NavTarget>.PermanentChild(
    permanentAppyxComponent: PermanentAppyxComponent<NavTarget>,
    navTarget: NavTarget,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val child by remember(navTarget, permanentAppyxComponent) {
        children
            .mapState(scope, SharingStarted.WhileSubscribed()) { childrenMap ->
                childrenMap
                    .keys
                    .find { it.interactionTarget == navTarget }
                    ?.let { childOrCreate(it) }
            }
    }.collectAsState()

    child?.let {
        it.node.Compose(modifier)
    }
}
