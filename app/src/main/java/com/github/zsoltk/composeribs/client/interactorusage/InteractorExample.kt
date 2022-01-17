package com.github.zsoltk.composeribs.client.interactorusage

import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.clienthelper.interactor.Interactor

class InteractorExample : Interactor() {

    override fun onLifecycleUpdated(state: Lifecycle.State) {
        if (state == Lifecycle.State.CREATED) {
            whenChildAttached(Child2Node::class) { _: Lifecycle, _: Child2Node ->
                (node as InteractorNode).child2InfoState = "Child2 has been attached"
            }

            whenChildAttached(Child3Node::class) { _: Lifecycle, _: Child3Node ->
                (node as InteractorNode).child3InfoState = "Child3 has been attached"
            }
        }
    }
}
