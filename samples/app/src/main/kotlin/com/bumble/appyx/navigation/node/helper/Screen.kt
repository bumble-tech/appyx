package com.bumble.appyx.navigation.node.helper

import androidx.compose.runtime.Composable
import com.bumble.appyx.navigation.composable.ScreenCenteredContent
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.node


internal fun screenNode(buildContext: BuildContext, content: @Composable () -> Unit): Node =
    node(buildContext) { modifier -> ScreenCenteredContent(modifier, content) }
