package com.bumble.appyx.sandbox.client.test

import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

fun <ViewModel : Any, Event : Any, View> appyxViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) where View : NodeView, View : Consumer<in ViewModel>, View : ObservableSource<out Event> =
    AppyxMviViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )
