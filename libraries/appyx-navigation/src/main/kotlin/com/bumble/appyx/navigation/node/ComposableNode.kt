package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext

open class ComposableNode(
    buildContext: BuildContext,
    private val composable: @Composable (Modifier) -> Unit
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        composable(modifier)
    }
}

fun node(buildContext: BuildContext, composable: @Composable (Modifier) -> Unit): Node =
    ComposableNode(buildContext, composable)
