package com.bumble.appyx.v2.sandbox.client.test

import com.bumble.appyx.testing.ui.rules.AppyxViewTestRule
import com.bumble.appyx.v2.core.node.NodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver

class AppyxMviViewTestRule<in ViewModel : Any, Event : Any, View : NodeView>(
    launchActivity: Boolean = true,
    private val modelConsumer: (View) -> Consumer<in ViewModel>,
    private val eventObservable: (View) -> ObservableSource<out Event>,
    viewFactory: ViewFactory<View>,
) : AppyxViewTestRule<View>(
    launchActivity = launchActivity,
    viewFactory = viewFactory,
) {
    private lateinit var _modelConsumer: Consumer<in ViewModel>

    val events: List<Event>
        get() = testEvents.values()
    val testEvents: TestObserver<Event> = TestObserver()

    override fun before() {
        super.before()
        runOnUiThread {
            _modelConsumer = modelConsumer(view)
            eventObservable(view).wrapToObservable().subscribe(testEvents)
        }
    }

    override fun after() {
        super.after()
        testEvents.dispose()
    }

    fun accept(v: ViewModel) {
        runOnUiThread { _modelConsumer.accept(v) }
    }
}
