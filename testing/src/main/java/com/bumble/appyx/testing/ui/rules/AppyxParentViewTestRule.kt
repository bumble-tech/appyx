package com.bumble.appyx.testing.ui.rules

import com.bumble.appyx.testing.ui.utils.DummyParentNode
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory

open class AppyxParentViewTestRule<Routing : Any, View : ParentNodeView<Routing>>(
    viewFactory: ViewFactory<View>
) : AppyxViewTestRule<View>(viewFactory) {

    override fun before() {
        super.before()
        runOnUiThread {
            val testNode = DummyParentNode<Routing>()
            view.init(testNode)
        }
    }

}
