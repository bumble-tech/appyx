package com.bumble.appyx.testing.view.rules

import androidx.test.rule.ActivityTestRule
import com.bumble.appyx.testing.view.utils.TestParentNode
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxParentViewTestRule<Routing : Any, View : ParentNodeView<Routing>>(
    private val launchActivity: Boolean = true,
    private var viewFactory: ViewFactory<View>,
) : ActivityTestRule<AppyxViewActivity>(
    /* activityClass = */ AppyxViewActivity::class.java,
    /* initialTouchMode = */ true,
    /* launchActivity = */ launchActivity
) {

    lateinit var view: View

    override fun apply(base: Statement, description: Description): Statement {
        val activityStatement = super.apply(base, description)
        return object : Statement() {
            override fun evaluate() {
                try {
                    setup()
                    activityStatement.evaluate()
                } finally {
                    reset()
                }
            }
        }
    }

    private fun setup() {
        view = viewFactory.invoke()

        AppyxViewActivity.initCallback = {
            val testNode = TestParentNode<Routing>()
            view.init(testNode)
        }
        AppyxViewActivity.view = view
    }

    private fun reset() {
        AppyxViewActivity.view = null
        AppyxViewActivity.initCallback = null
    }

    fun start() {
        require(!launchActivity) {
            "Activity will be launched automatically, launchActivity parameter was passed into constructor"
        }
        launchActivity(null)
    }

}
