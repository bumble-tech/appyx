package com.bumble.appyx.v2.app.node.helper

import androidx.compose.runtime.Composable
import com.bumble.appyx.v2.app.composable.ScreenCenteredContent
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.node


internal fun screenNode(buildContext: BuildContext, content: @Composable () -> Unit): Node =
    node(buildContext) { modifier -> ScreenCenteredContent(modifier, content) }
