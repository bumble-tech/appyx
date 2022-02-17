package com.bumble.appyx.interop.v1v2

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleEventObserver
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.interop.v1v2.V1V2View.Dependencies
import com.bumble.appyx.interop.v1v2.V1V2View.Factory
import com.bumble.appyx.v2.core.node.Node

class V1V2Node(
    buildParams: BuildParams<*>,
    val v2Node: Node
) : com.badoo.ribs.core.Node<V1V2View>(
    buildParams = buildParams,
    viewFactory = Factory().invoke(object : Dependencies {
        override val v2Node: Node = v2Node
    })
) {

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
