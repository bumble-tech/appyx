package com.bumble.appyx.testing.ui.rules

import com.bumble.appyx.testing.ui.utils.DummyParentNode
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory

open class AppyxParentViewTestRule<Routing : Any, View : ParentNodeView<Routing>>(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>
) : AppyxViewTestRule<View>(viewFactory, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        runOnUiThread {
            val testNode = DummyParentNode<Routing>()
            view.init(testNode)
        }
    }
}
