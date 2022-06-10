package com.bumble.appyx.v2.sandbox.client.test

import com.bumble.appyx.v2.core.node.NodeView
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

fun <Routing : Any, Model : Any, Event : Any, View> AppyxViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>
): AppyxMviParentViewTestRule<Routing, Model, Event, View> where View : ParentNodeView<Routing>, View : Consumer<in Model>, View : ObservableSource<out Event> =
    AppyxMviParentViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )

fun <Model : Any, Event : Any, View> AppyxViewRule(
    launchActivity: Boolean = true,
    viewFactory: ViewFactory<View>
): AppyxMviViewTestRule<Model, Event, View> where View : NodeView, View : Consumer<in Model>, View : ObservableSource<out Event> =
    AppyxMviViewTestRule(
        launchActivity = launchActivity,
        modelConsumer = { it },
        eventObservable = { it },
        viewFactory = viewFactory
    )
