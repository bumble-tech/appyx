package com.github.zsoltk.composeribs.core

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

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

//    @Composable
//    operator fun T.unaryPlus() {
//        key(this) {
//            children[this]?.Compose()
//        }
//    }

//    @Composable
//    inline fun <reified V : Any> V.compose() {
//        children
//            .filter { it::class.java.isAssignableFrom(V::class.java) }
//            .forEach {
//                it.value.Compose()
//            }
//    }

    @Composable
    inline operator fun <reified V : Any> Placeholder<V>.unaryPlus() {
        val filtered = remember(children, V::class.java) {
            children.filter { it.value.withView && it::class.java.isAssignableFrom(V::class.java) }
        }

        filtered.forEach {
            key(it.key) {
                // TODO get modifier for transition
                Box(modifier = Modifier) {
                    it.value.Compose()
                }
            }
        }
    }
}

class Placeholder<T>
