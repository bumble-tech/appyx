package com.bumble.appyx.interop.v1v2

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleEventObserver
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.interop.v1v2.V1V2View.Dependency
import com.bumble.appyx.interop.v1v2.V1V2View.Factory
import com.bumble.appyx.v2.core.node.Node

interface V1V2Node<N : Node> : Rib {
    val v2Node: N
}

internal class V1V2NodeImpl<N : Node>(
    buildParams: BuildParams<*>,
    override val v2Node: N
) : com.badoo.ribs.core.Node<V1V2View>(
    buildParams = buildParams,
    viewFactory = Factory<N>().invoke(object : Dependency<N> {
        override val v2Node: N = v2Node
    })
), V1V2Node<N> {

    private val observer = LifecycleEventObserver { source, _ ->
        v2Node.updateLifecycleState(source.lifecycle.currentState)
    }

    override fun onCreate() {
        super.onCreate()
        v2Node.updateLifecycleState(lifecycle.currentState)
        lifecycle.addObserver(observer)
    }

    override fun onDestroy(isRecreating: Boolean) {
        super.onDestroy(isRecreating)
        lifecycle.removeObserver(observer)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = v2Node.onSaveInstanceState { true }
        outState.putBundle(V1V2NodeKey, state.toBundle())
    }

    companion object {
        const val V1V2NodeKey = "V1V2NodeKey"
    }

    private fun Map<String, Any?>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())

}
