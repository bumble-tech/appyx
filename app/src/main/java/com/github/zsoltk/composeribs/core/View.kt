package com.github.zsoltk.composeribs.core

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf

abstract class RibView<T> {

    val children = mutableStateMapOf<T, ChildEntry>()

    @Composable
    internal fun Compose(withView: Boolean, children: List<T>) {
        Log.d("RibView", "Composing withView=$withView")
        if (withView) {
            Compose(children)
        } else {
            children.forEach { entry ->
//                entry.Compose(withView = false)
            }
        }
    }

    @Composable
    fun OffScreenChild() {

    }

    @Composable
    open fun Compose(children: List<T>) {}

    @Composable
    operator fun T.invoke() {
        key(this) {
            children[this]?.Compose()
        }
    }
}
