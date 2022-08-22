package com.bumble.appyx.testing.unit.common.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin

fun <N : Node> Interactor<N>.interactorTestHelperWithConnectable(
    nodeBuilder: (BuildContext, List<Plugin>) -> Node,
) = InteractorTestHelperWithConnectable(
    interactor = this,
    nodeBuilder = nodeBuilder,
)

class InteractorTestHelperWithConnectable<N : Node>(
    interactor: Interactor<N>,
    nodeBuilder: (BuildContext, List<Plugin>) -> Node,
) {
    private val node = nodeBuilder(
        BuildContext.root(savedStateMap = null),
        listOf(interactor),
    )

    private val nodeTestHelper = node.nodeTestHelper()

    fun moveTo(state: Lifecycle.State) {
        nodeTestHelper.moveTo(state)
    }

    /**
     * moves the Node to the desired state and then returns to the original state if possible
     */
    fun moveToStateAndCheck(state: Lifecycle.State, block: (Node) -> Unit) {
        nodeTestHelper.moveToStateAndCheck(state, block)
    }
}