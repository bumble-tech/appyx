package com.github.zsoltk.composeribs.client.interactorusage

import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.children.whenChildAttached
import com.github.zsoltk.composeribs.core.children.whenChildrenAttached
import com.github.zsoltk.composeribs.core.clienthelper.interactor.Interactor
import com.github.zsoltk.composeribs.core.lifecycle.subscribe

class InteractorExample : Interactor<InteractorExampleNode>() {

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.subscribe(onCreate = {
            whenChildAttached { _: Lifecycle, _: Child2Node ->
                node.child2InfoState = "Child2 has been attached"
            }
            whenChildrenAttached { _: Lifecycle, _: Child2Node, _: Child3Node ->
                node.child2And3InfoState = "Child2 and Child3 have been attached"
            }
        })
    }
}
