package com.github.zsoltk.composeribs.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.modality.BuildContext

open class ComposableNode(
    buildContext: BuildContext,
    private val composable: @Composable () -> Unit
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        composable()
    }
}

fun node(buildContext: BuildContext, composable: @Composable () -> Unit): Node =
    ComposableNode(buildContext, composable)
