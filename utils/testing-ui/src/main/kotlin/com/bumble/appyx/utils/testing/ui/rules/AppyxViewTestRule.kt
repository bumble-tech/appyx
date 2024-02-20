package com.bumble.appyx.utils.testing.ui.rules

import androidx.annotation.CallSuper
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.rule.ActivityTestRule
import com.bumble.appyx.navigation.node.LocalNode
import com.bumble.appyx.navigation.node.NodeView
import com.bumble.appyx.navigation.node.ViewFactory
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.utils.testing.ui.utils.DummyNode
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxViewTestRule<View : NodeView>(
    viewFactory: ViewFactory<View>,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule()
) : ActivityTestRule<AppyxTestActivity>(
    /* activityClass = */ AppyxTestActivity::class.java,
    /* initialTouchMode = */ true,
    /* launchActivity = */ false
), ComposeTestRule by composeTestRule {

    val view by lazy { viewFactory.invoke() }

    override fun apply(base: Statement, description: Description): Statement {
        val parent =
            TestRule { parentBase, parentDescription -> super.apply(parentBase, parentDescription) }

        return RunRules(base, listOf(composeTestRule, parent), description, ::before, ::after)
    }

    @Suppress("TooGenericExceptionCaught") // launchActivity does throw RuntimeException
    fun start() {
        try {
            launchActivity(null)
        } catch (e: RuntimeException) {
            throw IllegalStateException(
                "Unable to start Appyx test rule. Have you added 'debugImplementation " +
                        "\"com.bumble.appyx:testing-ui-activity:x.x.x\"' to your build.gradle?", e
            )
        }
    }

    override fun beforeActivityLaunched() {
        AppyxTestActivity.composableView = {
            CompositionLocalProvider(
                LocalNode provides DummyNode<Any>().build(),
            ) {
                view.Content(modifier = Modifier)
            }
        }
    }

    override fun afterActivityLaunched() {
        AppyxTestActivity.composableView = null
    }

    @CallSuper
    open fun before() {

    }

    @CallSuper
    open fun after() {

    }

}
