package com.bumble.appyx.v2.sandbox.client.test

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import com.bumble.appyx.v2.core.node.NodeView
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class AppyxViewParentRule<Routing : Any, ViewModel : Any, Event : Any, View>(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) : TestRule
    where View : ParentNodeView<Routing>, View : Consumer<in ViewModel>, View : ObservableSource<out Event> {

    val appyxRule = AppyxMviParentViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )

    val composeRule = AndroidComposeTestRule(appyxRule) { it.activity }

    override fun apply(base: Statement, description: Description): Statement {
        return composeRule.apply(base, description)
    }

}

class AppyxViewRule<ViewModel : Any, Event : Any, View>(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) : TestRule
    where View : NodeView, View : Consumer<in ViewModel>, View : ObservableSource<out Event> {

    val appyxRule = AppyxMviViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )

    val composeRule = AndroidComposeTestRule(appyxRule) { it.activity }

    override fun apply(base: Statement, description: Description): Statement {
        return composeRule.apply(base, description)
    }

}
