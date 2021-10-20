package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.Composable

open class ComposableNode(
    private val composable: @Composable () -> Unit
) : LeafNode(
    savedStateMap = null,
) {

    @Composable
    override fun View() {
        composable()
    }
}

fun node(composable: @Composable () -> Unit): Node<*> =
    ComposableNode(composable)
