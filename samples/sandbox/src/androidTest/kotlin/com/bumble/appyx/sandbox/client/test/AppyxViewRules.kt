package com.bumble.appyx.sandbox.client.test

import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

fun <NavTarget : Any, ViewModel : Any, Event : Any, View> appyxParentViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) where View : ParentNodeView<NavTarget>, View : Consumer<in ViewModel>, View : ObservableSource<out Event> =
    AppyxMviParentViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )

fun <NavTarget : Any, ViewModel : Any, Event : Any, View> appyxViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>,
) where View : ParentNodeView<NavTarget>, View : Consumer<in ViewModel>, View : ObservableSource<out Event> =
    AppyxMviViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )
