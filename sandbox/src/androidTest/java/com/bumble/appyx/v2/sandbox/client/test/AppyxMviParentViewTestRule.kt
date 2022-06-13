package com.bumble.appyx.v2.sandbox.client.test

import com.bumble.appyx.testing.ui.rules.AppyxParentViewTestRule
import com.bumble.appyx.v2.core.node.ParentNodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver

class AppyxMviParentViewTestRule<Routing: Any, in ViewModel : Any, Event : Any, View : ParentNodeView<Routing>>(
    launchActivity: Boolean = true,
    private val modelConsumer: (View) -> Consumer<in ViewModel>,
    private val eventObservable: (View) -> ObservableSource<out Event>,
    viewFactory: ViewFactory<View>,
) : AppyxParentViewTestRule<Routing, View>(
    launchActivity = launchActivity,
    viewFactory = viewFactory,
) {
    private lateinit var _modelConsumer: Consumer<in ViewModel>

    val events: List<Event>
        get() = testEvents.values()
    val testEvents: TestObserver<Event> = TestObserver()

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        runOnMainSync {
            _modelConsumer = modelConsumer(view)
            eventObservable(view).wrapToObservable().subscribe(testEvents)
        }
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()
        testEvents.dispose()
    }

    fun accept(v: ViewModel) {
        runOnMainSync { _modelConsumer.accept(v) }
    }
}
