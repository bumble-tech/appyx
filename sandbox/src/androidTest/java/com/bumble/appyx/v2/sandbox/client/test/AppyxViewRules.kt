package com.bumble.appyx.v2.sandbox.client.test

import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

fun <Routing : Any, ViewModel : Any, Event : Any, View> appyxParentViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) where View : ParentNodeView<Routing>, View : Consumer<in ViewModel>, View : ObservableSource<out Event> =
    AppyxMviParentViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )

fun <Routing : Any, ViewModel : Any, Event : Any, View> appyxViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) where View : ParentNodeView<Routing>, View : Consumer<in ViewModel>, View : ObservableSource<out Event> =
    AppyxMviViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )
