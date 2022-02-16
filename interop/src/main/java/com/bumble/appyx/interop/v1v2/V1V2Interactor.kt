package com.bumble.appyx.interop.v1v2

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.v2.core.node.Node

class V1V2Interactor<N : Node>(buildParams: BuildParams<*>) :
    Interactor<V1V2Node<N>, V1V2View<N>>(buildParams), NodeUpdateListener<N> {

    private var v2Node: N? = null
    private var observer: LifecycleEventObserver? = null

    override fun onDestroy() {
        observer?.let {
            node.lifecycle.removeObserver(it)
        }
    }

    override fun onViewCreated(view: V1V2View<N>, viewLifecycle: Lifecycle) {
        view.initialise(v2Node, this)
    }

    override fun onNodeUpdated(node: N) {
        val nodeLifecycle = this.node.lifecycle
        val observer = LifecycleEventObserver { source, _ ->
            node.updateLifecycleState(source.lifecycle.currentState)
        }
        node.updateLifecycleState(nodeLifecycle.currentState)
        nodeLifecycle.addObserver(observer)
        this.observer = observer
        v2Node = node
    }
}
