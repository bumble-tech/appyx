package com.bumble.appyx.v2.app.node.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.node


@Composable
internal fun Screen(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            content()
        }
    }
}

internal fun screenNode(buildContext: BuildContext, content: @Composable () -> Unit): Node =
    node(buildContext) { modifier -> Screen(modifier, content) }
