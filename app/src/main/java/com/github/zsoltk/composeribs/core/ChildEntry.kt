package com.github.zsoltk.composeribs.core

import android.util.Log
import androidx.compose.runtime.Composable

data class ChildEntry(
    val withView: Boolean,
    val rib: Node<*>
) {

    @Composable
    fun Compose(withView: Boolean = true) {
        Log.d("ChildEntry", "Composing withView=$withView, rib=$rib")
        rib.Compose(withView = withView && this.withView)
    }
}
