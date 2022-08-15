package com.bumble.appyx.interop.ribs

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleEventObserver
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.interop.ribs.InteropView.Dependency
import com.bumble.appyx.interop.ribs.InteropView.Factory
import com.bumble.appyx.core.node.Node

interface InteropNode<N : Node> : Rib {
    val appyxNode: N
}

internal class InteropNodeImpl<N : Node>(
    buildParams: BuildParams<*>,
    override val appyxNode: N
) : com.badoo.ribs.core.Node<InteropView>(
    buildParams = buildParams,
    viewFactory = Factory<N>().invoke(object : Dependency<N> {
        override val appyxNode: N = appyxNode
    })
), InteropNode<N> {

    private val observer = LifecycleEventObserver { source, _ ->
        appyxNode.updateLifecycleState(source.lifecycle.currentState)
    }

    override fun onCreate() {
        super.onCreate()
        appyxNode.updateLifecycleState(lifecycle.currentState)
        lifecycle.addObserver(observer)
    }

    override fun onDestroy(isRecreating: Boolean) {
        super.onDestroy(isRecreating)
        lifecycle.removeObserver(observer)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = appyxNode.saveInstanceState { true }
        outState.putBundle(InteropNodeKey, state.toBundle())
    }

    companion object {
        const val InteropNodeKey = "InteropNodeKey"
    }

    @Suppress("SpreadOperator")
    private fun Map<String, Any?>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())

}
