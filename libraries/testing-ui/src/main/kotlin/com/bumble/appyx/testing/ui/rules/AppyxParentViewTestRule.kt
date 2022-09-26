package com.bumble.appyx.testing.ui.rules

import com.bumble.appyx.testing.ui.utils.DummyParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.node.ViewFactory

open class AppyxParentViewTestRule<NavTarget : Any, View : ParentNodeView<NavTarget>>(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>
) : AppyxViewTestRule<View>(viewFactory, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        runOnUiThread {
            val testNode = DummyParentNode<NavTarget>()
            view.init(testNode)
        }
    }
}
