package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.modality.BuildContext

open class ComposableNode(
    buildContext: BuildContext,
    private val composable: @Composable () -> Unit
) : LeafNode(
    buildContext = buildContext,
) {

    @Composable
    override fun View() {
        composable()
    }
}

fun node(buildContext: BuildContext, composable: @Composable () -> Unit): Node<*> =
    ComposableNode(buildContext, composable)
