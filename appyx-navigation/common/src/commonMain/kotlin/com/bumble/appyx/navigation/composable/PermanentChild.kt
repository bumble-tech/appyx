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
fun <ChildReference : Any> ParentNode<ChildReference>.PermanentChild(
    permanentAppyxComponent: PermanentAppyxComponent<ChildReference>,
    reference: ChildReference,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val child by remember(reference, permanentAppyxComponent) {
        children
            .mapState(scope, SharingStarted.WhileSubscribed()) { childrenMap ->
                childrenMap
                    .keys
                    .find { it.interactionTarget == reference }
                    ?.let { childOrCreate(it) }
            }
    }.collectAsState()

    child?.let {
        it.node.Compose(modifier)
    }
}
