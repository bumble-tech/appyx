package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.StateObject

open class ComposableNode(
    private val composable: @Composable () -> Unit
) : LeafNode() {

    @Composable
    override fun View(foo: StateObject) {
        composable()
    }
}

fun node(composable: @Composable () -> Unit): Node<*> =
    ComposableNode(composable)
